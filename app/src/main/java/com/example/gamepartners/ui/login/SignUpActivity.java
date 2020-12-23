package com.example.gamepartners.ui.login;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamepartners.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView firstName;
    TextView lastName;
    TextView email;
    TextView password;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        email =findViewById(R.id.editTextEmail);
        password =findViewById(R.id.editTextPassword);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
//    public void signUp(View view) {
//        //final User currentuser = new User(firstName.getText().toString(),lastName.getText().toString(),phone.getText().toString(),address.getText().toString());
//        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                           String uid = mAuth.getCurrentUser().getUid();
//                            User newUser = new User(firstName.getText().toString(),lastName.getText().toString(),phone.getText().toString(),address.getText().toString());
//                            Map<String, User> users = new HashMap<>();
//                            users.put(uid,newUser);
//                            // Add a new document with a generated ID
//                            db.collection("users")
//                                    .add(users)
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                        @Override
//                                        public void onSuccess(DocumentReference documentReference) {
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                        }
//                                    });
//                            Toast.makeText(SignUpActivity.this,"Success",Toast.LENGTH_LONG).show();
//                            Intent intent =new Intent(SignUpActivity.this,MainActivity.class);
//                            startActivity(intent);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//    }
}