package com.example.gamepartners.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.format.Time;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game.Game;
import com.example.gamepartners.data.model.Game.GameAdapter;
import com.example.gamepartners.data.model.IJoinable;
import com.example.gamepartners.data.model.Post;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CreatePostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final int ADDRESS_PICKER_REQUEST = 50;
    private ArrayList<Game> games;
    private RecyclerView gamesRecyclerView;
    private GameAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    SearchView searchView;
    Button chooseDate, chooseTime, chooseLocation;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        chooseDate = findViewById(R.id.chooseDate);
        chooseTime = findViewById(R.id.chooseTime);
        chooseLocation = findViewById(R.id.chooseLocation);
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
        final Dialog dialog = new Dialog(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setClickable(false);

                dialog.setContentView(R.layout.dialog_post);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    private void showLocationPicker() {
        MapUtility.apiKey = getResources().getString(R.string.api_key);
        Intent intent =new Intent(this, LocationPickerActivity.class);
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
                recyclerViewAdapter.getFilter().filter(newText);
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
        games.add(new Game("Basketball",R.drawable.default_game, Game.ePlatform.REALITY));
        games.add(new Game("FIFA 21",R.drawable.default_game, Game.ePlatform.XBOX));
        games.add(new Game("Chess",R.drawable.default_game, Game.ePlatform.REALITY));
        games.add(new Game("GTA V Online",R.drawable.default_game, Game.ePlatform.PLAYSTATION));
        games.add(new Game("Soccer",R.drawable.default_game, Game.ePlatform.REALITY));
        games.add(new Game("Tennis",R.drawable.default_game, Game.ePlatform.REALITY));
        games.add(new Game("Call of Duty:WARZONE",R.drawable.default_game, Game.ePlatform.PC));
        games.add(new Game("Baseball",R.drawable.default_game, Game.ePlatform.REALITY));
        games.add(new Game("League of Legends",R.drawable.default_game, Game.ePlatform.PC));
        games.add(new Game("Table Tennis",R.drawable.default_game, Game.ePlatform.REALITY));
        games.add(new Game("NBA 2K21",R.drawable.default_game, Game.ePlatform.PLAYSTATION));
        games.add(new Game("Fortnite",R.drawable.default_game, Game.ePlatform.XBOX));
        games.add(new Game("Apex Legends",R.drawable.default_game, Game.ePlatform.PC));
        games.add(new Game("World of Warcraft",R.drawable.default_game, Game.ePlatform.PC));
        games.add(new Game("Red Dead Online",R.drawable.default_game, Game.ePlatform.PC));
        games.add(new Game("Beach Volleyball",R.drawable.default_game, Game.ePlatform.REALITY));
    }

    public void createPost(View view) {
        Game i_Game = new Game("FIFA21",0, Game.ePlatform.PC);
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        User user = new User(mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail());
        Post post = new Post(user,i_Game.getGameName(),0);
        //user.getUserPosts().add(post);
        reference.child(mAuth.getCurrentUser().getUid()).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                }
            }
        });
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
