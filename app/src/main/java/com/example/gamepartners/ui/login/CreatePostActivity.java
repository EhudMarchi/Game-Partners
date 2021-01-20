package com.example.gamepartners.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Comment;
import com.example.gamepartners.data.model.FirebaseUtills;
import com.example.gamepartners.data.model.Game.Game;
import com.example.gamepartners.data.model.Game.GameAdapter;
import com.example.gamepartners.data.model.IJoinable;
import com.example.gamepartners.data.model.Post;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CreatePostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final int ADDRESS_PICKER_REQUEST = 50;
    private ArrayList<Game> games;
    private RecyclerView gamesRecyclerView;
    private GameAdapter recyclerViewAdapter;
    public Game selectedGame;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    public ImageView selectedGameImage;
    public TextView selectedGameName;
    SearchView searchView;
    public Post post;
    Button chooseDate, chooseTime, chooseLocation;
    public Chip reality, pc, playstation, xbox;
    public boolean realityCheck = true, pcCheck = true, playstationCheck = true, xboxCheck = true;
    private ProgressDialog uploadProgress;
    String subject="", description="";
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        post= new Post();
        chooseDate = findViewById(R.id.chooseDate);
        chooseTime = findViewById(R.id.chooseTime);
        chooseLocation = findViewById(R.id.chooseLocation);
        selectedGameImage = findViewById(R.id.game_pic);
        selectedGameName = findViewById(R.id.selectedGameName);
        reality = findViewById(R.id.reality);
        pc = findViewById(R.id.pc);
        playstation = findViewById(R.id.playstation);
        xbox = findViewById(R.id.xbox);
        uploadProgress= new ProgressDialog(this);
        uploadProgress.setTitle("Uploading Post...");
        setFilters();
        final Animation zoominAnimation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.zoom_in);
        fillGames();
        setUpGamesRecyclerView();
        setSearchFilter();
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate.startAnimation(zoominAnimation);
                showDatePickerDialog();
            }
        });
        chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTime.startAnimation(zoominAnimation);
                showTimePickerDialog();
            }
        });
        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseLocation.startAnimation(zoominAnimation);
                showLocationPicker();
            }
        });
        final FloatingActionButton fab = findViewById(R.id.fabContinue);
        dialog = new Dialog(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setClickable(false);
                dialog.setContentView(R.layout.dialog_post);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                fab.setClickable(true);
            }
        });
        gamesRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGame = recyclerViewAdapter.getSelectedGame();
                refreshSelectedGame();
            }
        });
    }

    private void setFilters() {
        reality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realityCheck = !realityCheck;
                recyclerViewAdapter.getFilter("platform").filter("reality");
                if(realityCheck == true) {
                    reality.setAlpha(1f);
                }
                else
                {
                    reality.setAlpha(0.4f);
                }
            }
        });
        pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pcCheck = !pcCheck;
                recyclerViewAdapter.getFilter().filter("pc");
                if(pcCheck == true) {
                    pc.setAlpha(1f);
                }
                else
                {
                    pc.setAlpha(0.4f);
                }
            }
        });
        playstation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playstationCheck = !playstationCheck;
                recyclerViewAdapter.getFilter().filter("playstation");
                if(playstationCheck == true) {
                    playstation.setAlpha(1f);
                }
                else
                {
                    playstation.setAlpha(0.4f);
                }
            }
        });
        xbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xboxCheck = !xboxCheck;
                recyclerViewAdapter.getFilter().filter("xbox");
                if(xboxCheck == true) {
                    xbox.setAlpha(1f);
                }
                else
                {
                    xbox.setAlpha(0.4f);
                }
            }
        });
    }

    private void refreshSelectedGame() {
        if(selectedGame != null) {
            Glide.with(this).load(selectedGame.getGamePictureURL()).into(selectedGameImage);
        }
    }

    private void showLocationPicker() {
        MapUtility.apiKey = getResources().getString(R.string.api_key);
        //Intent intent =new Intent(this, LocationPickerActivity.class);
        //startActivityForResult(intent, ADDRESS_PICKER_REQUEST);
        Intent intent =new Intent(this, MapsActivity.class);
        startActivityForResult(intent, ADDRESS_PICKER_REQUEST);
    }

    private void setSearchFilter() {
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter("name").filter(newText);
                return false;
            }
        });
    }
    public void showTimePickerDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true);

                timePickerDialog.show();
    }
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private void setUpGamesRecyclerView() {
        searchView = (SearchView)findViewById(R.id.search);
        gamesRecyclerView = findViewById(R.id.gamesRecyclerView);
        gamesRecyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new GameAdapter(this,games);
        gamesRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        gamesRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private void fillGames() {
        games = new ArrayList<>();
        games.add(new Game("Basketball", "https://www.spalding.com/dw/image/v2/ABAH_PRD/on/demandware.static/-/Sites-masterCatalog_SPALDING/default/dwd21974bc/images/hi-res/74876E_FRONT.jpg?sw=555&sh=689&sm=cut&sfrm=jpg", Game.ePlatform.REALITY));
        games.add(new Game("FIFA 21","https://firebasestorage.googleapis.com/v0/b/gamepartners-app.appspot.com/o/games_images%2FFIFA%2021.jpg?alt=media&token=9364af73-bead-4837-8322-0ca2f3d64701", Game.ePlatform.XBOX));
        games.add(new Game("Chess","https://media.wired.com/photos/5f592bfb643fbe1f6e6807ec/191:100/w_2400,h_1256,c_limit/business_chess_1200074974.jpg", Game.ePlatform.REALITY));
        games.add(new Game("GTA V Online","FIF A21", Game.ePlatform.PLAYSTATION));
        games.add(new Game("Soccer","FIFA21", Game.ePlatform.REALITY));
        games.add(new Game("Tennis","FIFA21", Game.ePlatform.REALITY));
        games.add(new Game("Call of Duty:WARZONE","FIFA21", Game.ePlatform.PC));
        games.add(new Game("Baseball","FIFA21", Game.ePlatform.REALITY));
        games.add(new Game("League of Legends","FIFA21", Game.ePlatform.PC));
        games.add(new Game("Table Tennis","FIFA21", Game.ePlatform.REALITY));
        games.add(new Game("NBA 2K21","FIFA21", Game.ePlatform.PLAYSTATION));
        games.add(new Game("Fortnite","FIFA21", Game.ePlatform.XBOX));
        games.add(new Game("Apex Legends","FIFA21", Game.ePlatform.PC));
        games.add(new Game("World of Warcraft","FIFA21", Game.ePlatform.PC));
        games.add(new Game("Red Dead Online","FIFA21", Game.ePlatform.PC));
        games.add(new Game("Beach Volleyball","FIFA21", Game.ePlatform.REALITY));
    }

    public void createPost(final View view) {
        subject =((EditText)dialog.findViewById(R.id.subject)).getText().toString();
        description =((EditText)dialog.findViewById(R.id.description)).getText().toString();
        uploadProgress.show();
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        User user = new User(mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail());
        Post post = new Post(user,selectedGame, new Date(), subject, description,0,"Ashdod");
        reference.push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    dialog.dismiss();
                    Toast.makeText(CreatePostActivity.this, "Post Uploaded Successfully!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
//        reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("userPosts");
//        reference.push().setValue(post);
        uploadProgress.dismiss();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date =  dayOfMonth+ "/" + month+1 + "/" + year;
        chooseDate.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay+":"+ minute;
        chooseTime.setText(time);
    }
}
