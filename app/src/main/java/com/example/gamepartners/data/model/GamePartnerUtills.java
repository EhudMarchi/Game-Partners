package com.example.gamepartners.data.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.gamepartners.data.model.Game.Game;
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

import java.util.ArrayList;
import java.util.HashMap;

public class GamePartnerUtills {
    private  static GamePartnerUtills mSingleInstance = null;
    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    public static DatabaseReference myRef;
    public static User connedtedUser;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static String currentGameImage;
    public static ArrayList<Game> games;
    public static HashMap<String, String> userGroups;

    private GamePartnerUtills() {
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
    public static void createGroup(final String i_GroupName)
    {
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(i_GroupName);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("adminUID",mAuth.getUid());
        hashMap.put("groupName",i_GroupName);
        hashMap.put("chat",i_GroupName+mAuth.getUid());

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {

                }
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("groups").child(i_GroupName).child("groupFriends");
        HashMap<String, String> groupFriendsMap = new HashMap<>();
        groupFriendsMap.put("uid", GamePartnerUtills.connedtedUser.getUid());
        groupFriendsMap.put("firstName", GamePartnerUtills.connedtedUser.getFirstName());
        groupFriendsMap.put("lastName", GamePartnerUtills.connedtedUser.getLastName());
        groupFriendsMap.put("proflieImageURL", GamePartnerUtills.connedtedUser.getProflieImageURL());
        groupFriendsMap.put("uid",mAuth.getCurrentUser().getUid());
        reference.child(mAuth.getUid()).setValue(groupFriendsMap);
        reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("userGroups").child(i_GroupName);
        userGroups = new HashMap<>();
        userGroups.put("adminUID",mAuth.getUid());
        userGroups.put("groupName",i_GroupName);
        userGroups.put("chat",i_GroupName+mAuth.getUid());
        reference.setValue(userGroups);
    }
    public static GamePartnerUtills GetInstance()
    {
        if(mSingleInstance == null)
        {
            mSingleInstance = new GamePartnerUtills();
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
