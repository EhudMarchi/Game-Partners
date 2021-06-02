package com.example.gamepartners.controller;

import android.location.Address;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.gamepartners.data.model.Game;
import com.example.gamepartners.data.model.Group;
import com.example.gamepartners.data.model.Message;
import com.example.gamepartners.data.model.Post;
import com.example.gamepartners.data.model.Request;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GamePartnerUtills {
    private static GamePartnerUtills mSingleInstance = null;
    private static FirebaseAuth mAuth;
    public static DatabaseReference myRef;
    public static User connectedUser;
    private static HashMap<String,User> users;
    public static ArrayList<Game> games;
    public static String state = "";
    public static double latitude = 0, longitude = 0;
    public static Location location;
    public static Address selectedAddress = null;

    private GamePartnerUtills() {
        users = new HashMap<>();
        FetchAllData();
        myRef = FirebaseDatabase.getInstance().getReference();
        games = new ArrayList<>();
        fetchGames();
    }
    public static HashMap<String,User> getAllUsers()
    {
        return users;
    }
    public static void ChangeUserRequests(String i_Uid, ArrayList<Request> i_Requests)
    {
        users.get(i_Uid).setRequests(i_Requests);
    }
    public static void AddUserToGroup(User selectedUser, String groupID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("groups").child(groupID);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                assert group !=null;
                DatabaseReference reference;
                reference = FirebaseDatabase.getInstance().getReference("users").child(selectedUser.getUid()).child("userGroups");
                ArrayList<String> userGroups = selectedUser.getUserGroups();
                userGroups.add(group.getGroupID());
                reference.setValue(userGroups);
                reference = FirebaseDatabase.getInstance().getReference("groups").child(group.getGroupID()).child("groupFriends");
                ArrayList<User> groupFriends = (ArrayList<User>) group.getGroupFriends();
                groupFriends.add(selectedUser);
                reference.setValue(groupFriends);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    private void fetchGames() {
        myRef = FirebaseDatabase.getInstance().getReference().child("games");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void createGroup(final String i_GroupID, String i_GroupName, String groupImageURL) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(i_GroupID);
        Group newGroup = new Group(connectedUser.getUid(), i_GroupName, i_GroupID, groupImageURL);
        newGroup.getGroupFriends().add(connectedUser);
        reference.setValue(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("userGroups");
        ArrayList<String> userGroups = GamePartnerUtills.connectedUser.getUserGroups();
        userGroups.add(i_GroupID);
        reference.setValue(userGroups).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });
//        userGroups = new HashMap<>();
//        userGroups.put("adminUID", mAuth.getUid());
//        userGroups.put("groupName", i_GroupName);
//        userGroups.put("chat", i_GroupID + mAuth.getUid());
//        userGroups.put("groupImageURL", groupImageURL);
//        userGroups.put("groupFriends", newGroup.getGroupFriends());
//        userGroups.put("groupID", i_GroupID);
//        reference.setValue(userGroups);
    }
    public static void AddGroupMessage(User sender, String groupID, String i_Message , ArrayList<Message> i_ChatMessages)
    {
        final Message message = new Message(sender.getUid(), sender.getFirstName()+" "+sender.getLastName(), i_Message, Message.eMessageType.GROUP_MESSAGE);
        if (!message.getText().equals("")) {
            i_ChatMessages.add(message);
            DatabaseReference reference;
            reference = FirebaseDatabase.getInstance().getReference("groups").child(groupID).child("chat");
            reference.push().setValue(message);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    public static String getUserDisplayName(String uid)
    {
        return users.get(uid).getFirstName()+" "+users.get(uid).getLastName();
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

    public void FetchAllData()
    {
        users.clear();
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User tempUser;
                for (DataSnapshot user :snapshot.getChildren()) {
                    tempUser= user.getValue(User.class);
                    assert tempUser !=null;
                    users.put(tempUser.getUid(),tempUser);
                }
                connectedUser = GetUser(AuthInitialization().getCurrentUser().getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static boolean UpdateUserData(String i_FirstName, String i_LastName, String i_Password)
    {
        boolean isSuccessful = false;
        if((!i_FirstName.equals(""))&&(i_FirstName.matches( "[A-Z][a-z]*" ))&&(!i_LastName.equals(""))&&(i_LastName.matches( "[A-Z][a-z]*" ))&&(i_Password.length()>5))
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users").child(connectedUser.getUid()).child("firstName");
            myRef.setValue(i_FirstName);
            myRef = database.getReference("users").child(connectedUser.getUid()).child("lastName");
            myRef.setValue(i_LastName);
            myRef = database.getReference("users").child(connectedUser.getUid()).child("password");
            myRef.setValue(i_Password);
            isSuccessful = true;
        }

        return isSuccessful;
    }

    public static User GetUser(final String i_UserID)
    {
        return users.get(i_UserID);
    }
    public void UpdatePostLikes(Post i_Post)
    {

    }
}
