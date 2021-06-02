package com.example.gamepartners.ui.Activities_Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gamepartners.R;
import com.example.gamepartners.controller.Adapters.CommentAdapter;
import com.example.gamepartners.controller.Adapters.GameAdapter;
import com.example.gamepartners.controller.Adapters.RequestAdapter;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.controller.MyFirebaseMessagingService;
import com.example.gamepartners.data.model.Game;
import com.example.gamepartners.data.model.Post;
import com.example.gamepartners.controller.Adapters.PostAdapter;
import com.example.gamepartners.data.model.Request;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class UserProfileFragment extends Fragment implements GameAdapter.OnSelectedGamesChangeListener {
    TextView username, email, requests, favGames;
    ImageView imgViewProfilePic , profileSettingsPic;
    RelativeLayout settingsLayout;
    Button editProfile,logout;
    RecyclerView postsRecyclerView;
    ArrayList<Post> postsArrayList = new ArrayList<>();
    PostAdapter postAdapter;
    ArrayList<Game> games;
    Dialog requestsDialog, commentsDialog, editProfileDialog, favGamesDialog;
    public Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    Animation settingsAnimation;
    ImageButton removeButton;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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
        commentsDialog = new Dialog(getContext());
        commentsDialog.setContentView(R.layout.dialog_comments);
        commentsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestsDialog = new Dialog(getContext());
        requestsDialog.setContentView(R.layout.dialog_requests);
        requestsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        favGamesDialog = new Dialog(getContext());
        favGamesDialog.setContentView(R.layout.dialog_fav_games);
        favGamesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editProfileDialog = new Dialog(getContext());
        editProfileDialog.setContentView(R.layout.dialog_edit_profile);
        editProfileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        postsRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        postsRecyclerView.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter(this.getContext(), postsArrayList, commentsDialog);
        postsRecyclerView.setAdapter(postAdapter);
        setGamesToShow();
        try {
            Thread FetchThread = new Thread(new Runnable() {
                public void run() {
                    fetchLoggedInUserPosts();
                }
            });
            FetchThread.start();
            if (getView() != null) {
                getView().findViewById(R.id.loading_panel).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong..", Toast.LENGTH_LONG).show();
        }
        settingsAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.settings_in);
        final FloatingActionButton adminFab = getView().findViewById(R.id.fabAdmin);
        if (mAuth.getCurrentUser().getEmail().equals("tester1@gmail.com")) {
            adminFab.setVisibility(View.VISIBLE);
            adminFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(adminFab).navigate(R.id.action_homeFragment_to_adminFragment);
                }
            });
        }
        if(GamePartnerUtills.connectedUser!= null) {
            requests.setText(GamePartnerUtills.connectedUser.getRequests().size() + " Requests");
            if (GamePartnerUtills.connectedUser.getRequests().size() > 0) {
                requests.setEnabled(true);
                requests.setAlpha(1f);
            } else {
                requests.setEnabled(false);
                requests.setAlpha(0.4f);
            }
        }
    }
    private void fetchLoggedInUserPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Post post =ds.getValue(Post.class);
                    assert post !=null;
                    if(post.getPublisher().getEmail().equals(GamePartnerUtills.connectedUser.getEmail())) {
                        postsArrayList.add(post);
                    }
                }
                Collections.sort(postsArrayList, new Comparator<Post>() {
                    public int compare(Post first, Post second) {
                        return Long.valueOf(second.getTimePosted().getTime()).compareTo(first.getTimePosted().getTime());//sort Post from new to old
                    }
                });
                if(getContext()!=null) {
                    postAdapter = new PostAdapter(getContext(), postsArrayList, commentsDialog);
                    postsRecyclerView.setAdapter(postAdapter);
                }
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
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef= mStorage.getReference();
        imgViewProfilePic = view.findViewById(R.id.profile_pic);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        requests = view.findViewById(R.id.requests);
        favGames = view.findViewById(R.id.favGames);
        profileSettingsPic = view.findViewById(R.id.settings);
        settingsLayout = view.findViewById(R.id.settingsLayout);
        editProfile = view.findViewById(R.id.editProfile);
        logout = view.findViewById(R.id.logout);
        profileSettingsPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileSettingsPic.setClickable(false);
                if(settingsLayout.getVisibility() != View.VISIBLE) {
                    settingsLayout.setVisibility(View.VISIBLE);
                    settingsLayout.startAnimation(settingsAnimation);
                    settingsAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.settings_out);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            profileSettingsPic.setClickable(true);
                        }
                    },1000);
                }
                else
                {
                    settingsLayout.startAnimation(settingsAnimation);
                    settingsAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.settings_in);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            settingsLayout.setVisibility(View.GONE);
                            profileSettingsPic.setClickable(true);
                        }
                    },1000);

                }

            }
        });
        favGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    RecyclerView favGamesRecyclerView;
                    final GameAdapter favGamesAdapter;
                    favGames.setClickable(false);
                    favGamesRecyclerView = favGamesDialog.findViewById(R.id.favGamesRecyclerView);
                    ImageButton exitButton = favGamesDialog.findViewById(R.id.exit);
                    final ImageButton addButton = favGamesDialog.findViewById(R.id.add);

                    RecyclerView.LayoutManager requestsLayoutManager = new LinearLayoutManager(getContext());
                    favGamesRecyclerView.setLayoutManager(requestsLayoutManager);
                    favGamesAdapter = new GameAdapter(getContext(), GamePartnerUtills.connectedUser.getFavouriteGames(), true);
                    favGamesRecyclerView.setAdapter(favGamesAdapter);
                    favGamesAdapter.SetOnSelectedGamesChangeListener(UserProfileFragment.this);
                    removeButton = favGamesDialog.findViewById(R.id.removeFav);
                    removeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeFavGames(favGamesAdapter);
                            favGamesAdapter.notifyDataSetChanged();
                            setGamesToShow();
                            removeButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    favGamesDialog.show();
                    exitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            favGamesDialog.dismiss();
                        }
                    });
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog chooseGamesDialog = new Dialog(getContext());
                        chooseGamesDialog.setContentView(R.layout.dialog_choose_games);
                        chooseGamesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        RecyclerView allGamesRecyclerView;
                        final GameAdapter allGamesAdapter;
                        addButton.setClickable(false);
                        allGamesRecyclerView = chooseGamesDialog.findViewById(R.id.gamesSelectionRecyclerView);
                        final Button addSelectedButton = chooseGamesDialog.findViewById(R.id.addSelected);
                        RecyclerView.LayoutManager requestsLayoutManager = new LinearLayoutManager(getContext());
                        allGamesRecyclerView.setLayoutManager(requestsLayoutManager);
                        allGamesAdapter = new GameAdapter(getContext(), games, true);
                        allGamesRecyclerView.setAdapter(allGamesAdapter);
                        SearchView searchGamesForFav = chooseGamesDialog.findViewById(R.id.searchGamesForFav);
                        searchGamesForFav.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                allGamesAdapter.getFilter().filter(newText);
                                return false;
                            }
                        });
                        chooseGamesDialog.show();
                        addSelectedButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addSelectedButton.setClickable(false);
                                addFavGames(allGamesAdapter);
                                favGamesAdapter.notifyDataSetChanged();
                                chooseGamesDialog.dismiss();
                                addButton.setClickable(true);
                                setGamesToShow();
                                allGamesAdapter.notifyDataSetChanged();
                            }

                        });
                        addSelectedButton.setClickable(true);
                    }
                });
                addButton.setClickable(true);
                favGames.setClickable(true);
            }
        });
        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GamePartnerUtills.connectedUser.getRequests().size()>0) {
                    RecyclerView requestsRecyclerView;
                    RequestAdapter requestsAdapter;
                    requests.setClickable(false);
                    requestsRecyclerView = requestsDialog.findViewById(R.id.requests_recyclerView);
                    ImageButton exitButton = requestsDialog.findViewById(R.id.exit);
                    RecyclerView.LayoutManager requestsLayoutManager = new LinearLayoutManager(getContext());
                    requestsRecyclerView.setLayoutManager(requestsLayoutManager);
                    requestsAdapter = new RequestAdapter(getContext(), GamePartnerUtills.connectedUser.getRequests(), requestsDialog, requests);
                    requestsRecyclerView.setAdapter(requestsAdapter);
                    requestsDialog.show();
                    exitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestsDialog.dismiss();
                        }
                    });
                    requests.setClickable(true);
                }
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile.setClickable(false);
                editProfileDialog.show();
                final EditText firstName = editProfileDialog.findViewById(R.id.newFirstName);
                firstName.setText(GamePartnerUtills.connectedUser.getFirstName());
                final EditText lastName = editProfileDialog.findViewById(R.id.newLastName);
                lastName.setText(GamePartnerUtills.connectedUser.getLastName());
                final EditText password = editProfileDialog.findViewById(R.id.newPassword);
                password.setText(GamePartnerUtills.connectedUser.getPassword());
                Button confirm =  editProfileDialog.findViewById(R.id.confirm_changes);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(GamePartnerUtills.UpdateUserData(firstName.getText().toString(), lastName.getText().toString(), password.getText().toString()))
                        {
                            Toast.makeText(getContext(), "User Data Updated!", Toast.LENGTH_LONG).show();
                            editProfileDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Wrong input!\n first and last name cannot contain digits.\n password length should be at least 6 characters.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                editProfile.setClickable(true);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFirebaseMessagingService.unsubscribeUserToMessaging(mAuth.getUid());
                GamePartnerUtills.state = "LoggedOut";
                Navigation.findNavController(logout).navigate(R.id.action_homeFragment_to_loginFragment);
            }
        });
        imgViewProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Change Profile Picture?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                choosePicture();
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .create();
                dialog.show();
            }
        });
        fillUserData();
        return view;
    }
    private void setGamesToShow()
    {
        games = new ArrayList<>();

        for (Game game:GamePartnerUtills.games) {
            games.add(game);
            for (Game favGame:GamePartnerUtills.connectedUser.getFavouriteGames()) {
                if(favGame.getGameName().equals(game.getGameName()))
                {
                    games.remove(game);
                    break;
                }
            }
        }
    }
    private void addFavGames(GameAdapter adapter) {
                ArrayList<Game> favouriteGames = GamePartnerUtills.connectedUser.getFavouriteGames();
                for (Game selectedGame:adapter.getSelectedGames()) {
                    if(!favouriteGames.contains(selectedGame))
                    {
                        favouriteGames.add(selectedGame);
                    }
                }
                DatabaseReference favGamesRef = FirebaseDatabase.getInstance().getReference().child("users").child(GamePartnerUtills.connectedUser.getUid()).child("favouriteGames");
                favGamesRef.setValue(favouriteGames);
            }
    private void removeFavGames(GameAdapter adapter) {
        ArrayList<Game> favouriteGames = GamePartnerUtills.connectedUser.getFavouriteGames();
        for (Game selectedGame:adapter.getSelectedGames()) {
            if(favouriteGames.contains(selectedGame))
            {
                favouriteGames.remove(selectedGame);
            }
        }
        DatabaseReference favGamesRef = FirebaseDatabase.getInstance().getReference().child("users").child(GamePartnerUtills.connectedUser.getUid()).child("favouriteGames");
        favGamesRef.setValue(favouriteGames);
    }
    private void loadProfileImage(){
        mStorageRef = mStorage.getReference();
        mStorageRef.child("images/" + GamePartnerUtills.connectedUser.getEmail()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e("Url", "url: " + uri.toString());
                if(!uri.equals(null)) {
                    Glide.with(getContext()).load(uri.toString()).into(imgViewProfilePic);
                }
            }
        });

    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && data!= null && data.getData()!= null)
        {
            imageUri=data.getData();
            Glide.with(this).load(imageUri.toString()).into(imgViewProfilePic);
            GamePartnerUtills.connectedUser.setProflieImageURL(imageUri.toString());
            uploadProfilePic();
        }

    }

    private void uploadProfilePic() {
        final ProgressDialog uploadProgress = new ProgressDialog(getContext());
        uploadProgress.setTitle("Uploading Image...");
        uploadProgress.show();

        final StorageReference profilePicRef = mStorageRef.child("images/" + mAuth.getCurrentUser().getEmail());
        profilePicRef.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            uploadProgress.dismiss();
                            Snackbar.make(getView(), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    final DatabaseReference myRef = database.getReference("users").child(GamePartnerUtills.connectedUser.getUid()).child("proflieImageURL");
                                    myRef.setValue(uri.toString());
                                }
                            });

                        } else {
                            uploadProgress.dismiss();
                            Toast.makeText(getContext(), "Image Upload Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int progressPercent = (int) (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                uploadProgress.setMessage(progressPercent + "%");
            }
        });
    }

    private void fillUserData() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                email.setText(user.getEmail());
                username.setText(user.getFirstName()+" "+user.getLastName());
                if(!GamePartnerUtills.connectedUser.getProflieImageURL().equals("")) {
                    if(getContext()!=null) {
                        Glide.with(getContext()).load(GamePartnerUtills.connectedUser.getProflieImageURL()).into(imgViewProfilePic);
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
        postsArrayList.clear();
    }

    @Override
    public void OnSelectedGamesChanged(int selectedGamesAmount) {
        if(selectedGamesAmount>0)
        {
            removeButton.setVisibility(View.VISIBLE);
        }
        else
        {
            removeButton.setVisibility(View.INVISIBLE);

        }
    }
}
