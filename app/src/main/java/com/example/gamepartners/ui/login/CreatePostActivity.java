package com.example.gamepartners.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gamepartners.R;
import com.example.gamepartners.data.model.GamePartnerUtills;
import com.example.gamepartners.data.model.Game.Game;
import com.example.gamepartners.data.model.Game.GameAdapter;
import com.example.gamepartners.data.model.Post;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivtechs.maplocationpicker.MapUtility;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreatePostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    static final int PICK_MAP_POINT_REQUEST = 999;
    private ArrayList<Game> games;
    private RecyclerView gamesRecyclerView;
    private GameAdapter recyclerViewAdapter;
    public Game selectedGame;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    public ImageView selectedGameImage;
    public TextView selectedGameName;
    SearchView searchView;
    public Post post;
    Address selectedAddress;
    Button chooseDate, chooseTime, chooseLocation;
    public Chip reality, pc, playstation, xbox;
    int year, months,day,hour,minute;
    public boolean realityCheck = true, pcCheck = true, playstationCheck = true, xboxCheck = true;
    private ProgressDialog uploadProgress;
    CheckBox createGroupChat;
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
        fillGames();
        setUpGamesRecyclerView();
        setSearchFilter();
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        final Dialog imageView = new Dialog(this);
        imageView.setContentView(R.layout.game_image_view);
        imageView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectedGameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedGame!=null)
                {
                    selectedGameImage.setClickable(false);
                    if(selectedGame.getPlatforms().contains(Game.ePlatform.REALITY))
                    {
                        imageView.findViewById(R.id.realityIcon).setVisibility(View.VISIBLE);
                    }
                    else {
                        if (selectedGame.getPlatforms().contains(Game.ePlatform.PC)) {
                            imageView.findViewById(R.id.pcIcon).setVisibility(View.VISIBLE);
                        }
                        if (selectedGame.getPlatforms().contains(Game.ePlatform.PLAYSTATION)) {
                            imageView.findViewById(R.id.playstationIcon).setVisibility(View.VISIBLE);
                        }
                        if (selectedGame.getPlatforms().contains(Game.ePlatform.XBOX)) {
                            imageView.findViewById(R.id.xboxIcon).setVisibility(View.VISIBLE);
                        }
                    }
                    Glide.with(imageView.getContext()).load(selectedGame.getGamePictureURL()).into((ImageView) imageView.findViewById(R.id.game_pic));
                    imageView.show();
                    imageView.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            imageView.findViewById(R.id.realityIcon).setVisibility(View.GONE);
                            imageView.findViewById(R.id.pcIcon).setVisibility(View.GONE);
                            imageView.findViewById(R.id.playstationIcon).setVisibility(View.GONE);
                            imageView.findViewById(R.id.xboxIcon).setVisibility(View.GONE);
                        }
                    });
                    selectedGameImage.setClickable(true);
                }

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
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, PICK_MAP_POINT_REQUEST);
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
        games = GamePartnerUtills.games;
    }

    public void createPost(final View view) throws ParseException {
        subject =((EditText)dialog.findViewById(R.id.subject)).getText().toString();
        description =((EditText)dialog.findViewById(R.id.description)).getText().toString();
        createGroupChat =(CheckBox)dialog.findViewById(R.id.createGroupCheckBox);
        uploadProgress.show();
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        String postID = reference.push().getKey();
        if(chooseLocation.getText() != "Choose Location") {
            User user = new User(GamePartnerUtills.connedtedUser.getFirstName(), GamePartnerUtills.connedtedUser.getLastName(), mAuth.getCurrentUser().getEmail());
            Post post = new Post(postID, user, selectedGame, new Date(), subject, description, selectedAddress, new Date(year, months, day, hour, minute));
            reference.child(postID).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        finish();
                        Toast.makeText(getBaseContext(), "Post Uploaded Successfully!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
            if (createGroupChat.isChecked()) {
                GamePartnerUtills.createGroup(subject);
            }
        }
        else
        {
            Toast.makeText(getBaseContext(), "Please select location!",
                    Toast.LENGTH_LONG).show();
        }
//        reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("userPosts");
//        reference.push().setValue(post);
        uploadProgress.dismiss();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date =  dayOfMonth+ "/" + (int)(month+1) + "/" + year;
        chooseDate.setText(date);
        this.year = year;
        this.months = month;
        this.day = dayOfMonth;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String hours="";
        String minutes="";
        if(hourOfDay<10)
        {
            hours="0";
        }
        hours+=hourOfDay;
        if(minute<10)
        {
            minutes="0";
        }
        minutes+=minute;
        String time = hours+":"+minutes;
        chooseTime.setText(time);
        this.hour = hourOfDay;
        this.minute = minute;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MAP_POINT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                selectedAddress = (Address) data.getParcelableExtra("picked_location");
                chooseLocation.setText(selectedAddress.getAddressLine(0));
            }
        }
    }
}
