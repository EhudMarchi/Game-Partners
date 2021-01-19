package com.example.gamepartners.data.model;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gamepartners.R;
import com.example.gamepartners.ui.login.CreatePostActivity;
import com.example.gamepartners.ui.login.LoginActivity;
import com.example.gamepartners.ui.login.MainActivity;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    TextView username, email;
    ImageView imgViewProfilePic , profileSettingsPic;
    RelativeLayout settingsLayout;
    Button editProfile,logout;
    RecyclerView postsRecyclerView;
    ArrayList<Post> postsArrayList = new ArrayList<>();
    PostAdapter postAdapter;
    public Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    Animation settingsAnimation;

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
        postsRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        postsRecyclerView.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter(this.getContext(),postsArrayList);
        postsRecyclerView.setAdapter(postAdapter);
        populateRecycleView();
        settingsAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.settings_in);
    }

    private void populateRecycleView() {
        User user = new User("Loyal", "Pirate", "loyalpiratemusic@gmail.com", "123456",FirebaseUtills.connedtedUser.getProflieImageURL());
        Post post = new Post(user, Calendar.getInstance().getTime(),"Post 1 subject", "This is a post template",18,"Ashdod",new ArrayList<Comment>());
        postsArrayList.add(post);
        post = new Post(user,Calendar.getInstance().getTime(),"Post 2 subject", "This is a post template 2", 7,"Tel Aviv",new ArrayList<Comment>());
        postsArrayList.add(post);
        post = new Post(user,Calendar.getInstance().getTime(),"Post 3 subject", "This is a post template 3 ", 11,"Holon",new ArrayList<Comment>());
        postsArrayList.add(post);

        postAdapter.notifyDataSetChanged();
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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), LoginActivity.class);
                intent.putExtra("Mode","LoggedOut");
                startActivity(intent);
            }
        });
        if(FirebaseUtills.connedtedUser.getProflieImageURL().equals("")) {
            getProfileImage();
        }
        else
        {
            Glide.with(getContext()).load(FirebaseUtills.connedtedUser.getProflieImageURL()).into(imgViewProfilePic);
        }
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

    private void getProfileImage(){
        mStorageRef = mStorage.getReference();
        mStorageRef.child("images/" + FirebaseUtills.GetCurrentUser().getEmail()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
            FirebaseUtills.connedtedUser.setProflieImageURL(imageUri.toString());
            uploadProfilePic();
        }

    }

    private void uploadProfilePic() {
        final ProgressDialog uploadProgress= new ProgressDialog(getContext());
        uploadProgress.setTitle("Uploading Image...");
        uploadProgress.show();

        //final String randomKey = UUID.randomUUID().toString();
        StorageReference profilePicRef = mStorageRef.child("images/" + mAuth.getCurrentUser().getEmail());

        profilePicRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadProgress.dismiss();
                        Snackbar.make(getView(), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        uploadProgress.dismiss();
                        Toast.makeText(getContext(), "Image Upload Failed", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int progressPercent = (int)(100.0 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
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
                 //This method is called once with the initial value and again
                // whenever data at this location is updated.

                User user = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                email.setText(user.getEmail());
                username.setText(user.getFirstName()+" "+user.getLastName());
                //followers.setText(user.getFollowers().size());
                //following.setText(user.getFollowing().size());
                //}

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

}
