package com.example.gamepartners.ui.Activities_Fragments;

import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100 ;
    private static int SPLASH_SCREEN = 3250;
    private FirebaseAuth mAuth;
    GoogleSignInAccount googleAccount;
    EditText email;
    EditText password;
    ImageView logoImage;
    Animation fadeOutAnimation;
    Animation logoAnimation;
    Animation iconsAnimation;
    ArrayList<View> icons;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference myRef;
    SharedPreferences sharedPreferences;
    boolean isExist = false;
    private ProgressDialog signInProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getPreferences(MODE_PRIVATE);
        email =findViewById(R.id.editTextEmail);
        password =findViewById(R.id.editTextPassword);
        logoImage = findViewById(R.id.imageViewLogo);
        signInProgress = new ProgressDialog(this);
        signInProgress.setTitle("Signing in...");
        initializeIconsList();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email.setText(extras.getString("emailKey"));
            password.setText(extras.getString("passwordKey"));
            String mode = extras.getString("Mode");
            if(mode.equals("LoggedOut"))
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
            }
        }
        if (sharedPreferences.getString("KeyEmail", null) != null) {
            email.setText(sharedPreferences.getString("KeyEmail", null));
            password.setText(sharedPreferences.getString("KeyPassword", null));
            if(sharedPreferences.getString("KeyType", null).equals("Google")) {
                signUpWithGoogle(findViewById(R.id.buttonGoogle));
            }
            else {
                signIn(findViewById(R.id.buttonSignIn));
            }
        }
    }
    private void initializeIconsList() {
        icons = new ArrayList<View>();
        icons.add(findViewById(R.id.controller));
        icons.add(findViewById(R.id.controller2));
        icons.add(findViewById(R.id.baseball));
        icons.add(findViewById(R.id.basketball));
        icons.add(findViewById(R.id.table_tennis));
        icons.add(findViewById(R.id.soccer_field));
        icons.add(findViewById(R.id.chess));
        iconsAnimation = AnimationUtils.loadAnimation(this,R.anim.icons_start);
    }
    private void autoLogin(String type)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("KeyEmail",email.getText().toString());
        editor.putString("KeyPassword",password.getText().toString());
        editor.putString("KeyType",type);
        editor.apply();
    }
    private void createGoogleRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    private void transitionScreen()
    {
        signInProgress.dismiss();
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fadeout_animation);
        logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        findViewById(R.id.buttonSignIn).startAnimation(fadeOutAnimation);
        findViewById(R.id.buttonGoogle).startAnimation(fadeOutAnimation);
        findViewById(R.id.buttonFacebook).startAnimation(fadeOutAnimation);
        findViewById(R.id.textViewRegisterQuestion).startAnimation(fadeOutAnimation);
        findViewById(R.id.textViewRegister).startAnimation(fadeOutAnimation);
        email.startAnimation(fadeOutAnimation);
        password.startAnimation(fadeOutAnimation);
        logoImage.startAnimation(logoAnimation);
        for (View icon:icons) {
            icon.startAnimation(iconsAnimation);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendUserToMainActivity();
                finish();
            }
        },SPLASH_SCREEN);
    }

    public void signIn(View view) {

        if(!(email.getText().toString().equals(""))&&!(password.getText().toString().equals("")))
        {
            signInProgress.show();
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            autoLogin("Normal");
                            // Sign in success, update UI with the signed-in user's information
                            transitionScreen();
                            //sendUserToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            signInProgress.dismiss();
                        }
                    }
                });
        }
        else
        {
            Toast.makeText(LoginActivity.this, "Please enter valid email and password",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void signUp(View view) {
        Animation zoominAnimation = AnimationUtils.loadAnimation(this,R.anim.zoom_in);
        view.startAnimation(zoominAnimation);
        Intent intent =new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
    }
    public void signUpWithFacebook(View view)
    {
        Toast.makeText(LoginActivity.this, "Coming soon...",
                Toast.LENGTH_SHORT).show();
    }
    public void signUpWithGoogle(View view)
    {
        createGoogleRequest();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        autoLogin("Google");
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void checkIfGoogleAccountExist()
    {
        if(!isExist) {
            enterAccountPassword(this, googleAccount);
        }
        else
        {
            transitionScreen();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                googleAccount = account;
                myRef = FirebaseDatabase.getInstance().getReference().child("users");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            int i =0;
                            for (DataSnapshot user :dataSnapshot.getChildren()) {
                                Log.d("result", "iteration "+String.valueOf(i++));
                                //Log.d("result", user.getValue(User.class).getEmail());
                                Log.d("result", googleAccount.getEmail());
                                if(user.getValue(User.class).getEmail().equals(googleAccount.getEmail()))
                                {
                                    email.setText(googleAccount.getEmail());
                                    password.setText(user.getValue(User.class).getPassword());
                                    isExist = true;
                                    break;
                                }
                            }
                        checkIfGoogleAccountExist();


                            Log.d("result", "is exist: "+String.valueOf(isExist));
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }

    private void enterAccountPassword(Context context, final GoogleSignInAccount account) {
        final EditText taskEditText = new EditText(context);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Create Game Partners Account!")
                .setMessage("Enter Password:")
                .setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     writeNewGoogleUserToDB(mAuth.getCurrentUser().getUid(),account.getGivenName(),account.getFamilyName(),account.getEmail(),String.valueOf(taskEditText.getText()),account.getPhotoUrl().toString());
                     password.setText(String.valueOf(taskEditText.getText()));
                        transitionScreen();
                    }
                })
                .create();
        dialog.show();

    }

    private void firebaseAuthWithGoogle(String idToken) {
        signInProgress.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("result", "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("result", "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    private void writeNewGoogleUserToDB(String userId, String firstName, String lastName,String email, String password , String proflieImageURL) {
        // Write a Google user to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(userId);
        User newUser = new User(firstName, lastName, email, password , proflieImageURL);
        newUser.setUid(userId);
        myRef.setValue(newUser);
    }
    private  void sendUserToMainActivity()
    {
        Intent intent =new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

}