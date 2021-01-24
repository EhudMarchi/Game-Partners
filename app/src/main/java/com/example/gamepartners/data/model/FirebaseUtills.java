package com.example.gamepartners.data.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.gamepartners.data.model.Game.Game;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.ArrayList;

public class FirebaseUtills {
    private  static FirebaseUtills mSingleInstance = null;
    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    private static DatabaseReference myRef;
    public static User connedtedUser;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static String currentGameImage;

    private FirebaseUtills() {
        connedtedUser = GetUser(AuthInitialization().getCurrentUser().getUid());
    }

    public static FirebaseUtills GetInstance()
    {
        if(mSingleInstance == null)
        {
            mSingleInstance = new FirebaseUtills();
        }

        return mSingleInstance;
    }
    public static void AddFriend(String uid)
    {
        final ArrayList<String>[] friends = new ArrayList[]{new ArrayList<User>()};
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(GetCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends[0] = snapshot.getValue(User.class).getUserFriends();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friends[0].add(uid);
        myRef = database.getReference("users").child(GetCurrentUser().getUid()).child("userFriends");
        myRef.setValue(friends[0]);
    }
    public static void ChangeProfileImageUrl(String url)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(GetCurrentUser().getUid()).child("proflieImageURL");
        myRef.setValue(url);
    }
    public static FirebaseAuth AuthInitialization()
    {
        mAuth = FirebaseAuth.getInstance();

        return mAuth;
    }
    public static String GetGameImageURL(String gameName)
    {
        String hi = "";
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mStorageRef.child("games_images/" + gameName+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e("image", "url: " + uri.toString());
                if(!uri.equals(null)) {
                    currentGameImage = uri.toString();
                }
            }
        });
        Log.e("image", "game: " + currentGameImage);
        hi = currentGameImage;
        return hi;
    }
    public static FirebaseUser GetCurrentUser()
    {
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        return mUser;
    }

    public static User GetUser(final String i_UserID)
    {
        final User[] appUser = new User[1];
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i =0;
                for (DataSnapshot user :dataSnapshot.getChildren()) {
                    if(user.getValue(User.class).getUid().equals(i_UserID))
                    {
                        appUser[0] = user.getValue(User.class);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        return appUser[0];
    }
}
