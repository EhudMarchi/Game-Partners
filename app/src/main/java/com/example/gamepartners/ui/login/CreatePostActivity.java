package com.example.gamepartners.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game.Game;
import com.example.gamepartners.data.model.Game.GameAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CreatePostActivity extends AppCompatActivity {
    private ArrayList<Game> games;
    private RecyclerView gamesRecyclerView;
    private GameAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    SearchView searchView;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        fillGames();
        setUpGamesRecyclerView();
        setSearchFilter();
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

}
