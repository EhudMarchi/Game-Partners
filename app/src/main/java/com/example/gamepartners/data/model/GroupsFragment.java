package com.example.gamepartners.data.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Chat.Chat;
import com.example.gamepartners.ui.login.CreatePostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {
    private static final int WAIT = 1100;
    RecyclerView groupsRecyclerView;
    ArrayList<Group> groupsArrayList = new ArrayList<>();
    GroupsAdapter groupsAdapter;
    HashMap<String, String> userGroups;

    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
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
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        groupsRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        groupsRecyclerView.setHasFixedSize(true);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        FloatingActionButton fab = getView().findViewById(R.id.fab);

        groupsArrayList = new ArrayList<>();
        fetchGroups();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                        createNewGroup(taskEditText.getText().toString());
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                },WAIT);

            }
        });
    }

    private void createNewGroup(final String i_GroupName) {
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(i_GroupName);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("adminUID",mAuth.getUid());
        hashMap.put("groupName",i_GroupName);
        hashMap.put("chat",i_GroupName+mAuth.getUid());

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                }
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("userGroups").child(i_GroupName);
        userGroups = new HashMap<>();
        userGroups.put("adminUID",mAuth.getUid());
        userGroups.put("groupName",i_GroupName);
        userGroups.put("chat",i_GroupName+mAuth.getUid());
        reference.setValue(userGroups);
    }

    private void fetchGroups() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupsArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Group group =ds.getValue(Group.class);
                    assert group !=null;
                    groupsArrayList.add(group);
                }
                groupsAdapter = new GroupsAdapter(getContext(),groupsArrayList);
                groupsRecyclerView.setAdapter(groupsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        groupsArrayList.clear();
    }
}