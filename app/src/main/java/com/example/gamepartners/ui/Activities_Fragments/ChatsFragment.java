package com.example.gamepartners.ui.Activities_Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.gamepartners.R;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Group;
import com.example.gamepartners.controller.Adapters.GroupsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final int WAIT = 1100;
    RecyclerView chatsRecyclerView;
    GroupsAdapter chatsAdapter;
    ChatsFragmentViewModel viewModel;
    private static  boolean isChanged = false;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatsRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        chatsRecyclerView.setHasFixedSize(true);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        final FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setClickable(false);
                Snackbar.make(view, "Create New Group", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final EditText taskEditText = new EditText(getContext());
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setTitle("Enter Group Name:")
                                .setView(taskEditText)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GamePartnerUtills.createGroup(UUID.randomUUID().toString(),taskEditText.getText().toString(), "");
                                        if(GamePartnerUtills.connectedUser!=null) {
                                            fetchChats();
                                        }
                                    }
                                })
                                .create();
                        dialog.show();
                        fab.setClickable(true);
                    }
                },WAIT);

            }
        });
        viewModel = new ViewModelProvider(this).get(ChatsFragmentViewModel.class);
        Observer<ArrayList<Group>> chatsListObserver = new Observer<ArrayList<Group>>() {
            @Override
            public void onChanged(ArrayList<Group> groups) {
                chatsAdapter = new GroupsAdapter(getContext(), groups);
                chatsRecyclerView.setAdapter(chatsAdapter);
                chatsAdapter.notifyDataSetChanged();
            }
        };
        viewModel.getChats().observe(getViewLifecycleOwner(),chatsListObserver);
        chatsAdapter = new GroupsAdapter(getContext(), new ArrayList<>());
        chatsRecyclerView.setAdapter(chatsAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.chats_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                // Fetching chats from database
                if(GamePartnerUtills.connectedUser != null) {
                    fetchChats();
                    chatsAdapter.notifyDataSetChanged();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public static void notifyChatsChanged()
    {
        isChanged = true;
    }
    @Override
    public void onResume() {
        super.onResume();
        if(viewModel.getChats().getValue() == null || isChanged) {
            if(GamePartnerUtills.connectedUser!=null) {
                fetchChats();
            }
            isChanged = false;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(chatsAdapter!=null) {
                    chatsAdapter.notifyDataSetChanged();
                }
            }
        },100);
    }

    private void fetchChats() {
        ArrayList<Group> chatsArrayList = new ArrayList<>();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("groups");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group tempGroup;
                for (String group : GamePartnerUtills.connectedUser.getUserGroups()) {
                    tempGroup = snapshot.child(group).getValue(Group.class);
                    assert tempGroup !=null;
                    chatsArrayList.add(tempGroup);
                }
                if(getContext()!= null) {
                    viewModel.setChats(chatsArrayList);
                    if(chatsArrayList.size() == 0)
                    {
                        getView().findViewById(R.id.no_chats).setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        getView().findViewById(R.id.no_chats).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        // Fetching chats from database
        if(GamePartnerUtills.connectedUser != null) {
            fetchChats();
            chatsAdapter.notifyDataSetChanged();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }
}