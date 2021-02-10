package com.example.gamepartners.ui.Activities_Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        initializeFields();
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
            }
    private  void sendUserToLoginActivity()
    {
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        intent.putExtra("emailKey",email.getText().toString());
        intent.putExtra("passwordKey",password.getText().toString());
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
                                writeNewUserToDB(uid,firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString());

                                Toast.makeText(SignUpActivity.this, "Account Created", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                sendUserToLoginActivity();
                            }
                            else {
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
        // Write a user to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(userId);
        User newUser = new User(firstName, lastName, email, password,"");
        newUser.setUid(userId);
        myRef.setValue(newUser);
    }
}