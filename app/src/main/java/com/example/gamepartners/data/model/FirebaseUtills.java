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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;

public class FirebaseUtills {
    private  static FirebaseUtills mSingleInstance = null;
    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    public static DatabaseReference myRef;
    public static User connedtedUser;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static String currentGameImage;
    public static ArrayList<Game> games;

    private FirebaseUtills() {
        connedtedUser = GetUser(AuthInitialization().getCurrentUser().getUid());
        myRef = FirebaseDatabase.getInstance().getReference();
        games = new ArrayList<>();
        fetchGames();
    }

    private void fetchGames() {
        myRef = FirebaseDatabase.getInstance().getReference().child("games");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot game :dataSnapshot.getChildren()) {
                    Game libraryGame = game.getValue(Game.class);
                    assert libraryGame !=null;
                    Log.d("result", libraryGame.getGameName());
                    games.add(libraryGame);
                }
                Log.d("result", String.valueOf(games.size()));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
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
        DatabaseReference myRef = database.getReference("users").child(connedtedUser.getUid()).child("proflieImageURL");
        myRef.setValue(url);
    }
    public static FirebaseAuth AuthInitialization()
    {
        mAuth = FirebaseAuth.getInstance();

        return mAuth;
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
    public void UpdatePostLikes(Post i_Post)
    {

    }
}
