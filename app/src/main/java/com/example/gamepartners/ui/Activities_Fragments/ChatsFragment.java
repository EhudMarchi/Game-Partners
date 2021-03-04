package com.example.gamepartners.ui.Activities_Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {
    private static final int WAIT = 1100;
    RecyclerView chatsRecyclerView;
    ArrayList<Group> chatsArrayList = new ArrayList<>();
    GroupsAdapter chatsAdapter;
    HashMap<String, String> userGroups;

    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        fetchChats();
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
                                    }
                                })
                                .create();
                        dialog.show();
                        fab.setClickable(true);
                    }
                },WAIT);

            }
        });
    }

    private void fetchChats() {
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("userGroups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Group group =GamePartnerUtills.GetGroupByID(ds.getValue(String.class));
                    assert group !=null;
                    chatsArrayList.add(group);
                }
                if(getContext()!= null) {
                    try {
                    chatsAdapter = new GroupsAdapter(getContext(), chatsArrayList);
                    chatsRecyclerView.setAdapter(chatsAdapter);
                    }
                    catch (Exception e)
                    {
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
        chatsArrayList.clear();
    }
}