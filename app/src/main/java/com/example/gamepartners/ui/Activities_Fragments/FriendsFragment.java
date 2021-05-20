package com.example.gamepartners.ui.Activities_Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamepartners.R;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Request;
import com.example.gamepartners.data.model.User;
import com.example.gamepartners.controller.Adapters.UserAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsFragment extends Fragment {
    DatabaseReference reference;
    private ArrayList<User> friends;
    private ArrayList<User> users;
    private RecyclerView usersRecyclerView;
    private UserAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    ArrayList<User> selectedUsers;
    TextView selectedUserName;
    SearchView searchView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Button invite;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectedUserName = getView().findViewById(R.id.selectedGameName);
        friends = new ArrayList<>();
        invite = getView().findViewById(R.id.invite);
        setUpFriendsRecyclerView();
        setSearchFilter();
        usersRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUserName.setText(recyclerViewAdapter.getSelectedUser().getFirstName());
            }
        });
        final FloatingActionButton fab = getView().findViewById(R.id.fabSearch);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setClickable(false);
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_choose_friends);
                fetchAllUsers();
                RecyclerView allUsersRecyclerView = dialog.findViewById(R.id.friendsSelectionRecyclerView);
                final UserAdapter allUsersAdapter = new UserAdapter(dialog.getContext(), users, true);
                RecyclerView.LayoutManager allUsersRecyclerViewLayoutManager= new LinearLayoutManager(dialog.getContext());;
                allUsersRecyclerView.setLayoutManager(allUsersRecyclerViewLayoutManager);
                allUsersRecyclerView.setAdapter(allUsersAdapter);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button addSelected = dialog.findViewById(R.id.addSelected);
                SearchView searchUsersView = dialog.findViewById(R.id.searchUsers);
                searchUsersView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        allUsersAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
                addSelected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(v, "Add Selected", Snackbar.LENGTH_LONG).show();
                        selectedUsers = allUsersAdapter.getSelectedUsers();
                        for (User selectedUser:selectedUsers) {
                            Request friendRequest = new Request(Request.eRequestType.FRIEND, GamePartnerUtills.connectedUser.getUid(),
                                    GamePartnerUtills.connectedUser.getFirstName()+" "+GamePartnerUtills.connectedUser.getLastName(),
                                    selectedUser.getUid());
                            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(selectedUser.getUid()).child("requests").child(String.valueOf(GamePartnerUtills.GetUser(selectedUser.getUid()).getRequests().size()));
                            GamePartnerUtills.GetUser(selectedUser.getUid()).getRequests().add(friendRequest);
                            mRef.setValue(friendRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Request sent", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
                fab.setClickable(true);
                    }
                });
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Invite Friends");
                String app_url = " https://play.google.com/store/apps/details?id=my.example.javatpoint";
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,app_url);
                startActivity(Intent.createChooser(shareIntent, "Share Game Partners via"));
            }
        });
        fetchFriends();
        if(friends.size() == 0)
        {
            getView().findViewById(R.id.no_friends).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);

    }
    private void setSearchFilter() {
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void setUpFriendsRecyclerView() {
        searchView = (SearchView)getView().findViewById(R.id.search);
        usersRecyclerView = getView().findViewById(R.id.friendsRecyclerView);
        usersRecyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerViewAdapter = new UserAdapter(this.getContext(), friends, false);
        usersRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        usersRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private void fetchFriends() {
        final FirebaseAuth mAuth =FirebaseAuth.getInstance();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                    if(ds.getKey().equals("userFriends"))
                    {
                        reference.child("userFriends").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, String>>() {};
                                HashMap<String,String> uids = snapshot.getValue(genericTypeIndicator);
                                Log.d("friends",String.valueOf(uids.size()));
                                //friends.clear();
                                for (final String uid:uids.keySet()) {
                                    //Log.d("friends",uid);
                                    assert uid !=null;
                                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users");
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            friends.clear();
                                            User tempUser;
                                            for (DataSnapshot user :snapshot.getChildren()) {
                                                tempUser= user.getValue(User.class);
                                                assert tempUser !=null;
                                                if(tempUser.getUid().equals(uid) && !friends.contains(tempUser))
                                                {
                                                    Log.d("friends",tempUser.getUid());
                                                    friends.add(tempUser);
                                                    if(getView()!=null) {
                                                        getView().findViewById(R.id.no_friends).setVisibility(View.GONE);
                                                    }
                                                }
                                            }
                                            recyclerViewAdapter.notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        break;
                    }
                }
                Log.d("friends",String.valueOf(friends.size()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void fetchAllUsers() {
        users = new ArrayList<>();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot ds:snapshot.getChildren())
                {
                    User user = ds.getValue(User.class);
                    assert user !=null;
                    if(!user.getUid().equals(mAuth.getUid())&&(!isFriend(user.getUid())))
                    {
                        users.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private boolean isFriend(String uid)
    {
        boolean isFriend = false;
        for (User friend:friends) {
            if(friend.getUid().equals(uid))
            {
                isFriend = true;
            }
        }
        return isFriend;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        friends.clear();
    }
}