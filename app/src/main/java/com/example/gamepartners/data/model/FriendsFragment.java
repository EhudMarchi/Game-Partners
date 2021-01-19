package com.example.gamepartners.data.model;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game.Game;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    DatabaseReference reference;
    private ArrayList<User> users;
    private RecyclerView usersRecyclerView;
    private UserAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    TextView selectedUserName;
    SearchView searchView;
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
        invite = getView().findViewById(R.id.invite);
        fillFriends();
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
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_choose_friends);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewAdapter = new UserAdapter(getContext(), users, false);
        usersRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        usersRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private void fillFriends() {
        users = new ArrayList<>();
        final FirebaseAuth mAuth =FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    assert user !=null;
                    if(!user.getUid().equals(mAuth.getCurrentUser().getUid())) {
                        //**HERE CHECK IF FRIENDS***
                        users.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}