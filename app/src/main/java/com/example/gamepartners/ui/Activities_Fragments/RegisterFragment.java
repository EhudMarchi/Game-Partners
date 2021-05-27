package com.example.gamepartners.ui.Activities_Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gamepartners.R;
import com.example.gamepartners.controller.MyFirebaseMessagingService;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    Button signUpButton;
    private ProgressDialog loadingBar;


    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeFields();
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    private void initializeFields() {
        firstName = ((TextInputLayout)getActivity().findViewById(R.id.editTextFirstName)).getEditText();
        lastName = ((TextInputLayout)getActivity().findViewById(R.id.editTextLastName)).getEditText();
        email = ((TextInputLayout)getActivity().findViewById(R.id.registerEditTextEmail)).getEditText();
        password =((TextInputLayout)getActivity().findViewById(R.id.registerEditTextPassword)).getEditText();
        loadingBar = new ProgressDialog(getContext());
        signUpButton = getActivity().findViewById(R.id.buttonSignUp);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }
    private  void sendUserToLoginActivity()
    {
        /*
        Intent intent = new Intent(getContext(),LoginActivity.class);
        intent.putExtra("emailKey",email.getText().toString());
        intent.putExtra("passwordKey",password.getText().toString());
        intent.putExtra("Mode","SignedUp");
        startActivity(intent);
         */
        Navigation.findNavController(signUpButton).navigate(R.id.action_registerFragment_to_homeFragment);
    }

    public void signUp() {
        if (TextUtils.isEmpty(firstName.getText())) {
            Toast.makeText(getContext(), "Please enter first name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(lastName.getText())) {
            Toast.makeText(getContext(), "Please enter last name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(email.getText())) {
            Toast.makeText(getContext(), "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password.getText())) {
            Toast.makeText(getContext(), "Please enter password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information
                                String uid = mAuth.getCurrentUser().getUid();
                                writeNewUserToDB(uid, firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString());
                                Toast.makeText(getContext(), "Account Created", Toast.LENGTH_LONG).show();
                                MyFirebaseMessagingService.subscribeUserToMessaging(uid);
                                loadingBar.dismiss();
                                sendUserToLoginActivity();
                            } else {
                                // If sign in fails, display a message to the user.
                                String message = task.getException().toString();
                                Toast.makeText(getContext(), "Authentication failed - " + message,
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