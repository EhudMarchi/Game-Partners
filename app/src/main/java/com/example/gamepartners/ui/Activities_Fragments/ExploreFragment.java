package com.example.gamepartners.ui.Activities_Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamepartners.R;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Post;
import com.example.gamepartners.controller.Adapters.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.util.Arrays.stream;

public class ExploreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final int WAIT = 1100;
    public RecyclerView postsRecyclerView;
    ArrayList<Post> postsArrayList = new ArrayList<>();
    PostAdapter postAdapter;
    SearchView searchView;
    int postMaxDistance;
    private TextView distanceTextView;
    Dialog commentsDialog;
    boolean favouriteFilterOn = false;
    SwipeRefreshLayout mSwipeRefreshLayout;
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

    private void fetchPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    assert post != null;
                        if ((post.getPublisher().getUid().equals(GamePartnerUtills.connectedUser.getUid())) || (!post.isPrivate()) || (GamePartnerUtills.connectedUser.getUserFriends().containsKey(post.getPublisher().getUid()))) {
                            {
                                postsArrayList.add(post);
                            }
                        }
                }
                Collections.sort(postsArrayList, new Comparator<Post>() {
                    public int compare(Post first, Post second) {
                        return Long.valueOf(second.getTimePosted().getTime()).compareTo(first.getTimePosted().getTime());//sort Post from new to old
                    }
                });
                try {
                getView().findViewById(R.id.loading_panel).setVisibility(View.GONE);
                if(postsArrayList.size()<1)
                {
                getView().findViewById(R.id.no_posts).setVisibility(View.VISIBLE);
                }
                }
                catch (Exception e){}
                postAdapter.notifyDataSetChanged();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = (SearchView)getView().findViewById(R.id.searchPost);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                postAdapter.setSerachFilter(newText);
                return false;
            }
        });
        commentsDialog = new Dialog(getContext());
        commentsDialog.setContentView(R.layout.dialog_comments);
        commentsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Thread postsFetchThread = new Thread(new Runnable() {
            public void run() {
                fetchPosts();
            }
        });
        postsFetchThread.start();
        setRecycleView();
        setDistanceDialog();
        final FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setClickable(false);
                Snackbar.make(view, "Create New Game Post", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Navigation.findNavController(fab).navigate(R.id.action_homeFragment_to_createPostFragment);
                        fab.setClickable(true);
                    }
                }, WAIT);
            }
        });
        setCommentsRecycleView();
        final FloatingActionButton favouriteFilter = getView().findViewById(R.id.fabFavouriteFilter);
        favouriteFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favouriteFilterOn = !favouriteFilterOn;
                postAdapter.setFavFilter(favouriteFilterOn);
                if(favouriteFilterOn) {
                    Toast.makeText(getContext(), "Favourite Games Filter On",
                            Toast.LENGTH_SHORT).show();
                    favouriteFilter.setAlpha(1f);
                }
                else
                {
                    Toast.makeText(getContext(), "Favourite Games Filter Off",
                            Toast.LENGTH_SHORT).show();
                    favouriteFilter.setAlpha(0.4f);
                }
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                // Fetching posts from database
                fetchPosts();
                postAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setCommentsRecycleView() {

    }

    private void setDistanceDialog() {
        final FloatingActionButton distanceFilter = getView().findViewById(R.id.fabDistanceFilter);
        final Dialog distanceDialog = new Dialog(getContext());
        distanceDialog.setContentView(R.layout.dialog_distance_filter);
        distanceTextView = distanceDialog.findViewById(R.id.distance);
        distanceTextView.setText("0");
        distanceFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(!gps_enabled) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("You must enable the GPS service on your device")
                            .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .show();
                }
                else {
                    distanceFilter.setClickable(false);
                    final SeekBar distanceSeekbar = distanceDialog.findViewById(R.id.seekBarDistance);
                    distanceSeekbar.setProgress((int) distanceSeekbar.getMax() / 2);
                    distanceTextView.setText(String.valueOf(distanceSeekbar.getProgress()) + " km");
                    distanceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            postMaxDistance = progress;
                            distanceTextView.setText(postMaxDistance + " km");
                            if (progress == distanceSeekbar.getMax()) {

                                distanceTextView.setText("Any distance");
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                    Button filter = distanceDialog.findViewById(R.id.filterButton);
                    filter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Thread postsFetchThread = new Thread(new Runnable() {
//                                public void run() {
//                                    fetchPosts();
//                                }
//                            });
//                            postsFetchThread.start();
//                            try {
//                                postsFetchThread.join();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            if (!distanceTextView.getText().toString().equals("Any distance"))
                                {
                                    postAdapter.setDistanceFilter(postMaxDistance);
                                }
                                else
                                {
                                    postAdapter.setDistanceFilter(-1);
                                }
                            if (postAdapter.getItemCount() == 0) {
                                getView().findViewById(R.id.no_posts).setVisibility(View.VISIBLE);
                            } else {
                                getView().findViewById(R.id.no_posts).setVisibility(View.GONE);
                            }
                            distanceDialog.dismiss();
                        }
                    });
                    distanceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    distanceDialog.show();
                    distanceFilter.setClickable(true);
                }
            }});
    }

    private void setRecycleView() {
        postsRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        postsRecyclerView.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter(this.getContext(), postsArrayList,commentsDialog);
        postsRecyclerView.setAdapter(postAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        postsArrayList.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            public void run() {
                fetchPosts();
            }
        }).run();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        // Fetching posts from database
        fetchPosts();
        postAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}