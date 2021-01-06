package com.example.gamepartners.data.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseUtills {
    private  static FirebaseUtills mSingleInstance = null;
    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    public static FirebaseUtills GetInstance()
    {
        if(mSingleInstance == null)
        {
            mSingleInstance = new FirebaseUtills();
        }

        return mSingleInstance;
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
}
