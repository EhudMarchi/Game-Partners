package com.example.gamepartners.data.model;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.gamepartners.R;
import com.example.gamepartners.ui.login.CreatePostActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.shivtechs.maplocationpicker.MapUtility.LATITUDE;
import static com.shivtechs.maplocationpicker.MapUtility.LONGITUDE;
import static com.shivtechs.maplocationpicker.MapUtility.currentLocation;

public class ExploreFragment extends Fragment {
    private static final int WAIT = 1100;
    RecyclerView postsRecyclerView;
    ArrayList<Post> postsArrayList = new ArrayList<>();
    PostAdapter postAdapter;
    SearchView searchView;
    public ExploreFragment() {
        // Required empty public constructor
    }

    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void populateRecycleView() {
        Post post = new Post(new User("Ehud", "Marchi", "Ehud@gmail.com", "123456","https://cdn1.iconfinder.com/data/icons/user-pictures/100/supportmale-256.png"), Calendar.getInstance().getTime(),"Post 1 subject", "This is a post template",18,"Ashdod",new ArrayList<Comment>());
        postsArrayList.add(post);
        post = new Post(new User("Yossi", "Cohen", "Yosi@gmail.com", "13513","https://cdn0.iconfinder.com/data/icons/user-pictures/100/matureman1-256.png"),Calendar.getInstance().getTime(),"Post 2 subject", "This is a post template 2", 7,"Tel Aviv",new ArrayList<Comment>());
        postsArrayList.add(post);
        post = new Post(new User("Avraham", "Levi", "AVVI@gmail.com", "143436","https://cdn0.iconfinder.com/data/icons/user-pictures/100/maturewoman-3-256.png"),Calendar.getInstance().getTime(),"Post 3 subject", "This is a post template 3 ", 11,"Holon",new ArrayList<Comment>());
        postsArrayList.add(post);
        post = new Post(new User("Dana", "Meron", "danam@gmail.com", "00020225","https://cdn0.iconfinder.com/data/icons/user-pictures/100/supportfemale-2-256.png"),Calendar.getInstance().getTime(),"Post 4 subject", "This is a post template 4 ", 22,"Jerusalem",new ArrayList<Comment>());
        postsArrayList.add(post);
        post = new Post(new User("James", "Williams", "jwil@gmail.com", "00sdgds5","https://cdn1.iconfinder.com/data/icons/santa-emojis/60/004_-_santa_christmas_emoji_cool_sunglasses-256.png"),Calendar.getInstance().getTime(),"Post 5 subject", "This is a post template 5 ", 22,"Sderot",new ArrayList<Comment>());
        postsArrayList.add(post);
        post = new Post(new User("Leo", "Messi", "leomessi@gmail.com", "barca454","https://cdn3.iconfinder.com/data/icons/soccer-uniform/128/barcelona.png"),Calendar.getInstance().getTime(),"Post 6 subject", "This is a post template 6 ", 22,"Barcelona",new ArrayList<Comment>());
        postsArrayList.add(post);
        //postAdapter.notifyDataSetChanged();
    }
    private void fetchPosts() {
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Post post =ds.getValue(Post.class);
                    assert post !=null;
                    postsArrayList.add(post);
                }
                postAdapter = new PostAdapter(getContext(),postsArrayList);
                postsRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = (SearchView)getView().findViewById(R.id.search);
        postsRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        postsRecyclerView.setLayoutManager(layoutManager);
        final FloatingActionButton fab = getView().findViewById(R.id.fab);
        postAdapter = new PostAdapter(this.getContext(), postsArrayList);
        postsRecyclerView.setAdapter(postAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setClickable(false);
                Snackbar.make(view, "Create New Game Post", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getContext(), CreatePostActivity.class);
                        startActivity(intent);
                        fab.setClickable(true);
                    }
                }, WAIT);

            }
        });
        //fetchPosts();
        populateRecycleView();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        postsArrayList.clear();
    }
}