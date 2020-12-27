package com.example.gamepartners.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100 ;
    private FirebaseAuth mAuth;
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    DatabaseReference myRef;
    FirebaseDatabase database;
    private ProgressDialog loadingBar;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        initializeFields();
        //database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("message");
    }



    private void initializeFields() {
        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        email =findViewById(R.id.editTextEmail);
        password =findViewById(R.id.editTextPassword);
        loadingBar = new ProgressDialog(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    private  void sendUserToLoginActivity()
    {
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
    }



    public void signUp(View view) {
        if(TextUtils.isEmpty(email.getText()))
        {
            Toast.makeText(SignUpActivity.this,"Please enter email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password.getText()))
        {
            Toast.makeText(SignUpActivity.this,"Please enter password",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information
                                String uid = mAuth.getCurrentUser().getUid();
                                //writeNewUserToDB(uid,firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString());

                                Toast.makeText(SignUpActivity.this, "Account Created", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                sendUserToLoginActivity();
                            } else {
                                // If sign in fails, display a message to the user.
                                String message = task.getException().toString();
                                Toast.makeText(SignUpActivity.this, "Authentication failed - " + message,
                                        Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });
        }
    }
    private void writeNewUserToDB(String userId, String firstName, String lastName,String email, String password) {
        myRef = database.getReference("Users").child(userId);
        User newUser = new User(firstName, lastName, email, password);
        myRef.setValue(newUser);

        Toast.makeText(SignUpActivity.this, "Added to db", Toast.LENGTH_LONG).show();
    }
}