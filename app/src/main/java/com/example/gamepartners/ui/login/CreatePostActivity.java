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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.example.gamepartners.R;
import com.example.gamepartners.data.model.FirebaseUtills;
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
    Game selectedGame;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    ImageView selectedGameImage;
    SearchView searchView;
    Button chooseDate, chooseTime, chooseLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        chooseDate = findViewById(R.id.chooseDate);
        chooseTime = findViewById(R.id.chooseTime);
        chooseLocation = findViewById(R.id.chooseLocation);
        selectedGameImage = findViewById(R.id.game_pic);
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
        gamesRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGame = recyclerViewAdapter.getSelectedGame();
                refreshSelectedGame();
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
        games.add(new Game("Basketball", "https://www.spalding.com/dw/image/v2/ABAH_PRD/on/demandware.static/-/Sites-masterCatalog_SPALDING/default/dwd21974bc/images/hi-res/74876E_FRONT.jpg?sw=555&sh=689&sm=cut&sfrm=jpg", Game.ePlatform.REALITY));
        games.add(new Game("FIFA 21","FIFA21", Game.ePlatform.XBOX));
        games.add(new Game("Chess","FIFA21", Game.ePlatform.REALITY));
        games.add(new Game("GTA V Online","FIFA21", Game.ePlatform.PLAYSTATION));
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

    public void createPost(View view) {
        Game i_Game = new Game("FIFA21",FirebaseUtills.GetGameImageURL("FIFA21"), Game.ePlatform.PC);
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
