package com.example.gamepartners.data.model;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gamepartners.R;
import com.example.gamepartners.ui.login.CreatePostActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }
    private void populateRecycleView() {
        Group group = new Group("Group1");
        groupsArrayList.add(group);
        group = new Group("Group2");
        groupsArrayList.add(group);
        group = new Group("Group3");
        groupsArrayList.add(group);
        groupsAdapter.notifyDataSetChanged();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        groupsRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        groupsRecyclerView.setLayoutManager(layoutManager);
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        groupsAdapter = new GroupsAdapter(this.getContext(),groupsArrayList);
        groupsRecyclerView.setAdapter(groupsAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Create New Group", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Intent intent =new Intent(getContext(), CreatePostActivity.class);
                        //startActivity(intent);
                    }
                },WAIT);

            }
        });
        populateRecycleView();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        groupsArrayList.clear();
    }
}