package com.example.gamepartners.data.model;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gamepartners.R;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {
    RecyclerView postsRecyclerView;
    ArrayList<Post> postsArrayList = new ArrayList<>();
    ExploreAdapter exploreAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
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
//        postsRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
//        postsRecyclerView.setLayoutManager(layoutManager);
//
//        exploreAdapter = new ExploreAdapter(this.getContext(),postsArrayList);
//        postsRecyclerView.setAdapter(exploreAdapter);
//
//        populateRecycleView();

    }

    private void populateRecycleView() {
        Post post = new Post(new User("Ehud", "Marchi", "Ehud@gmail.com", "123456"), Calendar.getInstance().getTime(), "This is a post template", 18,new ArrayList<Comment>());
        postsArrayList.add(post);
        post = new Post(new User("Yossi", "Cohen", "Yosi@gmail.com", "13513"),Calendar.getInstance().getTime(), "This is a post template 2", 7,new ArrayList<Comment>());
        postsArrayList.add(post);
        post = new Post(new User("Avraham", "Levii", "AVVI@gmail.com", "143436"),Calendar.getInstance().getTime(), "This is a post template 3 ", 11,new ArrayList<Comment>());
        postsArrayList.add(post);
        exploreAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postsRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        postsRecyclerView.setLayoutManager(layoutManager);

        exploreAdapter = new ExploreAdapter(this.getContext(),postsArrayList);
        postsRecyclerView.setAdapter(exploreAdapter);

        populateRecycleView();
    }
}