package com.example.gamepartners.ui.Activities_Fragments;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.gamepartners.controller.Adapters.GameAdapter;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Game;
import com.example.gamepartners.data.model.Post;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivtechs.maplocationpicker.MapUtility;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CreatePostFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener , MapsFragment.OnLocationPickedListener{

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
    Button chooseDate, chooseTime, chooseLocation;
    public Chip reality, pc, playstation, xbox;
    int year, months,day,hour,minute;
    public boolean realityCheck = true, pcCheck = true, playstationCheck = true, xboxCheck = true;
    private ProgressDialog uploadProgress;
    CheckBox privatePost;
    String subject="", description="";
    Dialog dialog;
    public CreatePostFragment() {
        // Required empty public constructor
    }

    public static CreatePostFragment newInstance(String param1, String param2) {
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        post= new Post();
        chooseDate = getActivity().findViewById(R.id.chooseDate);
        chooseTime = getActivity().findViewById(R.id.chooseTime);
        chooseLocation = getActivity().findViewById(R.id.chooseLocation);
        selectedGameImage = getActivity().findViewById(R.id.game_pic);
        selectedGameName = getActivity().findViewById(R.id.selectedGameName);
        reality = getActivity().findViewById(R.id.reality);
        pc = getActivity().findViewById(R.id.pc);
        playstation = getActivity().findViewById(R.id.playstation);
        xbox = getActivity().findViewById(R.id.xbox);
        uploadProgress= new ProgressDialog(getContext());
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
        final FloatingActionButton fab = getActivity().findViewById(R.id.fabContinue);
        dialog = new Dialog(getContext());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedGame!=null) {
                    if (chooseTime.getText().equals("Choose Time") || chooseDate.getText().equals("Choose Date")) {
                        Toast.makeText(getActivity().getBaseContext(), "Please choose date and time!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        if (!chooseLocation.getText().equals("Choose Location")) {
                            fab.setClickable(false);
                            dialog.setContentView(R.layout.dialog_post);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Button post = dialog.findViewById(R.id.post);
                            post.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    createPost();
                                }
                            });
                            dialog.show();
                            fab.setClickable(true);
                        }
                        else {
                            Toast.makeText(getActivity().getBaseContext(), "Please select location!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(getActivity().getBaseContext(), "Please select game!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        gamesRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGame = recyclerViewAdapter.getSelectedGame();
                refreshSelectedGame();
            }
        });
        final Dialog imageView = new Dialog(getContext());
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void setFilters() {
        reality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realityCheck = !realityCheck;
                recyclerViewAdapter.getFilter().filter(searchView.getQuery().toString());
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
                recyclerViewAdapter.getFilter().filter(searchView.getQuery().toString());
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
                recyclerViewAdapter.getFilter().filter(searchView.getQuery().toString());
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
                recyclerViewAdapter.getFilter().filter(searchView.getQuery().toString());
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
        MapsFragment chooseLocationFragment = new MapsFragment(this);
        chooseLocationFragment.show(getActivity().getSupportFragmentManager(),"");
        //Navigation.findNavController(chooseLocation).navigate(R.id.action_createPostFragment_to_mapsFragment);
    }

    private void setSearchFilter() {
        searchView = (SearchView)getView().findViewById(R.id.search);
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
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true);

        timePickerDialog.show();
    }
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private void setUpGamesRecyclerView() {
        gamesRecyclerView = getActivity().findViewById(R.id.gamesRecyclerView);
        gamesRecyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewAdapter = new GameAdapter(getContext(),games, CreatePostFragment.this);
        gamesRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        gamesRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private void fillGames() {
        games = GamePartnerUtills.games;
    }

    public void createPost(){
        subject = ((EditText) dialog.findViewById(R.id.subject)).getText().toString();
        description = ((EditText) dialog.findViewById(R.id.description)).getText().toString();
        privatePost = (CheckBox) dialog.findViewById(R.id.privatePostCheckBox);
        uploadProgress.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        String postID = reference.push().getKey();

        final User user = GamePartnerUtills.connectedUser;
        Post post = new Post(postID, user, selectedGame, new Date(), subject, description, GamePartnerUtills.selectedAddress, new Date(year, months, day, hour, minute), privatePost.isChecked());
        user.getUserPosts().add(postID);
        reference.child(postID).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(getActivity().getBaseContext(), "Post Uploaded Successfully!",
                            Toast.LENGTH_LONG).show();
                    DatabaseReference updateMyPostsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("postsID");
                    updateMyPostsRef.setValue(user.getUserPosts());
                    //Navigation.findNavController(selectedGameName).navigate(R.id.action_createPostFragment_to_homeFragment);
                    getActivity().onBackPressed();
                }
            }
        });
        GamePartnerUtills.createGroup(postID, post.getSubject(), post.getGame().getGamePictureURL());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    @Override
    public void onLocationPicked(Address selectedAddress) {
        chooseLocation.setText(selectedAddress.getAddressLine(0).split(",")[0]);
        GamePartnerUtills.selectedAddress = selectedAddress;
    }
}