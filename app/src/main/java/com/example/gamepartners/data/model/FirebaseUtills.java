package com.example.gamepartners.data.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseUtills {
    private  static FirebaseUtills mSingleInstance = null;
    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    private static DatabaseReference myRef;
    public static User connedtedUser;
    public static FirebaseUtills GetInstance()
    {
        if(mSingleInstance == null)
        {
            mSingleInstance = new FirebaseUtills();
        }

        return mSingleInstance;
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
