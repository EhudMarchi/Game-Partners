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
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Post;
import com.example.gamepartners.controller.Adapters.PostAdapter;
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
    Dialog commentsDialog;
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
        commentsDialog = new Dialog(getContext());
        commentsDialog.setContentView(R.layout.dialog_comments);
        commentsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        postsRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        postsRecyclerView.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter(this.getContext(), postsArrayList,commentsDialog);
        postsRecyclerView.setAdapter(postAdapter);
        try {
        Thread postsFetchThread = new Thread(new Runnable() {
            public void run() {
                fetchLoggedInUserPosts();

            }
        });
        postsFetchThread.start();
        postsFetchThread.join();
        getView().findViewById(R.id.loading_panel).setVisibility(View.GONE);
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), "Something went wrong..", Toast.LENGTH_LONG).show();
        }
        settingsAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.settings_in);
        FloatingActionButton adminFab = getView().findViewById(R.id.fabAdmin);
        if(mAuth.getCurrentUser().getEmail().equals("loyalpiratemusic@gmail.com"))
        {
            adminFab.setVisibility(View.VISIBLE);
            adminFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(getContext(), AdminActivity.class);
                    startActivity(intent);
                }
            });
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
                getActivity().finish();
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
        //final String randomKey = UUID.randomUUID().toString();
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
                if(GamePartnerUtills.connectedUser.getProflieImageURL().equals("")) {
                    loadProfileImage();
                }
                else
                {
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

}
