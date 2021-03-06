package com.example.gamepartners.ui.Activities_Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamepartners.R;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.controller.MyFirebaseMessagingService;
import com.example.gamepartners.data.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 100 ;
    private static int SPLASH_SCREEN = 3250;
    private FirebaseAuth mAuth;
    GoogleSignInAccount googleAccount;
    FirebaseUser fbUser;
    EditText email;
    EditText password;
    ImageView logoImage;
    TextView signUpTextView;
    Animation fadeOutAnimation;
    Animation logoAnimation;
    Animation iconsAnimation;
    ArrayList<View> icons;
    Button signInButton , googleButton;
    LoginButton facebookButton;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference myRef;
    SharedPreferences sharedPreferences;
    boolean isExist = false;
    private ProgressDialog signInProgress;
    CallbackManager callbackManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPreferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        email =((TextInputLayout)getActivity().findViewById(R.id.editTextEmail)).getEditText();
        password =((TextInputLayout)getActivity().findViewById(R.id.editTextPassword)).getEditText();
        signInButton = getView().findViewById(R.id.buttonSignIn);
        googleButton = getView().findViewById(R.id.buttonGoogle);
        facebookButton = getView().findViewById(R.id.buttonFacebook);
        logoImage = getActivity().findViewById(R.id.imageViewLogo);
        signInProgress = new ProgressDialog(getContext());
        signInProgress.setTitle("Signing in...");
        initializeIconsList();
        if(GamePartnerUtills.state.equals("LoggedOut"))
        {
            LoginManager.getInstance().logOut();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            email.setText(extras.getString("emailKey"));
            password.setText(extras.getString("passwordKey"));

        }
        if (sharedPreferences.getString("KeyEmail", null) != null) {
            email.setText(sharedPreferences.getString("KeyEmail", null));
            password.setText(sharedPreferences.getString("KeyPassword", null));
            if(sharedPreferences.getString("KeyType", null).equals("Google")) {
                signUpWithGoogle();
            }
            else if(sharedPreferences.getString("KeyType", null).equals("Facebook")) {
                fbUser = FirebaseAuth.getInstance().getCurrentUser();
                GamePartnerUtills.state = "SignedIn";
                MyFirebaseMessagingService.subscribeUserToMessaging(mAuth.getUid());
                Navigation.findNavController(signInButton).navigate(R.id.action_loginFragment_to_homeFragment);
            }
            else {
                GamePartnerUtills.state = "SignedIn";
                MyFirebaseMessagingService.subscribeUserToMessaging(mAuth.getUid());
                Navigation.findNavController(signInButton).navigate(R.id.action_loginFragment_to_homeFragment);
            }
        }
        signUpTextView = getActivity().findViewById(R.id.textViewRegister);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(v);
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpWithGoogle();
            }
        });
        callbackManager = CallbackManager.Factory.create();
        facebookButton.setPermissions(Arrays.asList("public_profile","email"));
        facebookButton.setFragment(this);
        if(facebookButton.getText().equals("Log out"))
        {
            autoLogin("Facebook");
        }
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    public void checkIfFacebookAccountExist()
    {
        if(!isExist) {
            enterFBAccountPassword(getContext(), fbUser);
        }
        else
        {
            transitionScreen();
        }
    }
    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(getContext(), "CONNECTED TO FACEBOOK", Toast.LENGTH_SHORT).show();
                fbUser = FirebaseAuth.getInstance().getCurrentUser();
                isExist = !task.getResult().getAdditionalUserInfo().isNewUser();
                checkIfFacebookAccountExist();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(FacebookSdk.getApplicationContext());
    }
    private void initializeIconsList() {
        icons = new ArrayList<View>();
        icons.add(getActivity().findViewById(R.id.controller));
        icons.add(getActivity().findViewById(R.id.controller2));
        icons.add(getActivity().findViewById(R.id.baseball));
        icons.add(getActivity().findViewById(R.id.basketball));
        icons.add(getActivity().findViewById(R.id.table_tennis));
        icons.add(getActivity().findViewById(R.id.soccer_field));
        icons.add(getActivity().findViewById(R.id.chess));
        iconsAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.icons_start);
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

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(),gso);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    private void transitionScreen()
    {
        signInProgress.dismiss();
        fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout_animation);
        logoAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.logo_animation);
        getActivity().findViewById(R.id.buttonSignIn).startAnimation(fadeOutAnimation);
        getActivity().findViewById(R.id.buttonGoogle).startAnimation(fadeOutAnimation);
        getActivity().findViewById(R.id.buttonFacebook).startAnimation(fadeOutAnimation);
        getActivity(). findViewById(R.id.textViewRegisterQuestion).startAnimation(fadeOutAnimation);
        getActivity().findViewById(R.id.textViewRegister).startAnimation(fadeOutAnimation);
        ((TextInputLayout)getActivity().findViewById(R.id.editTextEmail)).startAnimation(fadeOutAnimation);;
        ((TextInputLayout)getActivity().findViewById(R.id.editTextPassword)).startAnimation(fadeOutAnimation);;
        logoImage.startAnimation(logoAnimation);
        for (View icon:icons) {
            icon.startAnimation(iconsAnimation);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GamePartnerUtills.state = "SignedIn";
                MyFirebaseMessagingService.subscribeUserToMessaging(mAuth.getUid());
                Navigation.findNavController(signInButton).navigate(R.id.action_loginFragment_to_homeFragment);
            }
        },SPLASH_SCREEN);
    }

    public void signIn() {

        if(!(email.getText().toString().equals(""))&&!(password.getText().toString().equals("")))
        {
            signInProgress.show();
            mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                autoLogin("Normal");
                                // Sign in success, update UI with the signed-in user's information
                                transitionScreen();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                signInProgress.dismiss();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(getContext(), "Please enter valid email and password",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void signUpWithGoogle()
    {
        createGoogleRequest();
        autoLogin("Google");
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }
    public void checkIfGoogleAccountExist()
    {
        if(!isExist) {
            enterAccountPassword(getContext(), googleAccount);
        }
        else
        {
            transitionScreen();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        myRef = FirebaseDatabase.getInstance().getReference().child("users");
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int i =0;
//                for (DataSnapshot user :dataSnapshot.getChildren()) {
//                    if(user.getValue(User.class).getUid().equals(mAuth.getUid()))
//                    {
//                        email.setText(mAuth.getCurrentUser().getEmail());
//                        password.setText(user.getValue(User.class).getPassword());
//                        isExist = true;
//                        break;
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//            }
//        });
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "SUCCESS!", Toast.LENGTH_SHORT).show();
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        final GoogleSignInAccount account = task.getResult(ApiException.class);
                        googleAccount = account;
                        firebaseAuthWithGoogle(account.getIdToken());

                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        // ...
                    }
                } else Toast.makeText(getContext(), "FAIL!", Toast.LENGTH_SHORT).show();
            }
        else
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void signUp(View view) {
        Animation zoominAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.zoom_in);
        view.startAnimation(zoominAnimation);
        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
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
                        writeNewUserToDB(mAuth.getCurrentUser().getUid(),account.getGivenName(),account.getFamilyName(),account.getEmail(),String.valueOf(taskEditText.getText()),account.getPhotoUrl().toString());
                        password.setText(String.valueOf(taskEditText.getText()));
                        email.setText(account.getEmail());
                        transitionScreen();
                    }
                })
                .create();
        dialog.show();

    }
    private void enterFBAccountPassword(Context context, final FirebaseUser fbUser) {
        final EditText taskEditText = new EditText(context);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Create Game Partners Account!")
                .setMessage("Enter Password:")
                .setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        writeNewUserToDB(mAuth.getCurrentUser().getUid(),fbUser.getDisplayName().split(" ")[0],fbUser.getDisplayName().split(" ")[1],fbUser.getEmail(),String.valueOf(taskEditText.getText()),fbUser.getPhotoUrl().toString()+"?type=large");
                        password.setText(String.valueOf(taskEditText.getText()));
                        email.setText(mAuth.getCurrentUser().getEmail());
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
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            isExist = !task.getResult().getAdditionalUserInfo().isNewUser();
                            checkIfGoogleAccountExist();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("result", "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    private void writeNewUserToDB(String userId, String firstName, String lastName,String email, String password , String proflieImageURL) {
        // Write a Google/Facebook user to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(userId);
        User newUser = new User(firstName, lastName, email, password , proflieImageURL);
        newUser.setUid(userId);
        myRef.setValue(newUser);
        MyFirebaseMessagingService.subscribeUserToMessaging(userId);
    }

}