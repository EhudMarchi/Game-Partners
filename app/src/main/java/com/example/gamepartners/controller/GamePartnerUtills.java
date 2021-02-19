package com.example.gamepartners.controller;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.gamepartners.data.model.Game;
import com.example.gamepartners.data.model.Group;
import com.example.gamepartners.data.model.Post;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePartnerUtills {
    private static GamePartnerUtills mSingleInstance = null;
    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    public static DatabaseReference myRef;
    public static User connectedUser;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static String currentGameImage;
    private static User searchedUser;
    private static ArrayList<User> users;
    public static ArrayList<Game> games;
    public static HashMap<String, String> userGroups;

    private GamePartnerUtills() {
        connectedUser = GetUser(AuthInitialization().getCurrentUser().getUid());
        myRef = FirebaseDatabase.getInstance().getReference();
        games = new ArrayList<>();
        fetchGames();
    }

    private void fetchGames() {
        myRef = FirebaseDatabase.getInstance().getReference().child("games");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot game : dataSnapshot.getChildren()) {
                    Game libraryGame = game.getValue(Game.class);
                    assert libraryGame != null;
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
    public static float getKmFromLatLong(float lat1, float lng1, float lat2, float lng2) {
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);
        float distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters / 1000;
    }

    public static void createGroup(final String i_GroupID, String i_GroupName) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(i_GroupID);
        Group newGroup = new Group(connectedUser, i_GroupName, i_GroupID);
        reference.setValue(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("groups").child(i_GroupID).child("groupFriends");
        HashMap<String, String> groupFriendsMap = new HashMap<>();
        groupFriendsMap.put("uid", GamePartnerUtills.connectedUser.getUid());
        groupFriendsMap.put("firstName", GamePartnerUtills.connectedUser.getFirstName());
        groupFriendsMap.put("lastName", GamePartnerUtills.connectedUser.getLastName());
        groupFriendsMap.put("proflieImageURL", GamePartnerUtills.connectedUser.getProflieImageURL());
        groupFriendsMap.put("email", GamePartnerUtills.connectedUser.getEmail());
        groupFriendsMap.put("uid", mAuth.getCurrentUser().getUid());
        reference.child(mAuth.getUid()).setValue(groupFriendsMap);
        reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("userGroups").child(i_GroupID);
        userGroups = new HashMap<>();
        userGroups.put("adminUID", mAuth.getUid());
        userGroups.put("groupName", i_GroupName);
        userGroups.put("chat", i_GroupID + mAuth.getUid());
        userGroups.put("groupID", i_GroupID);
        reference.setValue(userGroups);
    }

    public static GamePartnerUtills GetInstance() {
        if (mSingleInstance == null) {
            mSingleInstance = new GamePartnerUtills();
        }

        return mSingleInstance;
    }

    public static void ChangeProfileImageUrl(String url) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(connectedUser.getUid()).child("proflieImageURL");
        myRef.setValue(url);
    }

    public static FirebaseAuth AuthInitialization() {
        mAuth = FirebaseAuth.getInstance();

        return mAuth;
    }

    public static FirebaseUser GetCurrentUser() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        return mUser;
    }

    public static User GetUser(final String i_UserID)
    {
        users = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User tempUser;
                for (DataSnapshot user :snapshot.getChildren()) {
                    tempUser= user.getValue(User.class);
                    assert tempUser !=null;
                    users.add(tempUser);
                    if(tempUser.getUid().equals(i_UserID))
                    {
                        searchedUser = tempUser;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return searchedUser;
    }
    public void UpdatePostLikes(Post i_Post)
    {

    }
}
