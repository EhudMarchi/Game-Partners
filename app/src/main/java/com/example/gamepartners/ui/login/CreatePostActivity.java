package com.example.gamepartners.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game;
import com.example.gamepartners.data.model.GameAdapter;

import java.util.ArrayList;

public class CreatePostActivity extends AppCompatActivity {
    private ArrayList<Game> games;
    private RecyclerView gamesRecyclerView;
    private GameAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        fillGames();
        SearchView searchView = findViewById(R.id.search);
        gamesRecyclerView = findViewById(R.id.gamesRecyclerView);
        gamesRecyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new GameAdapter(this,games);
        gamesRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        gamesRecyclerView.setAdapter(recyclerViewAdapter);

    }

    private void fillGames() {
        games = new ArrayList<>();
        games.add(new Game("Basketball",R.drawable.default_game));
        games.add(new Game("FIFA 21",R.drawable.default_game));
        games.add(new Game("Chess",R.drawable.default_game));
        games.add(new Game("GTA V Online",R.drawable.default_game));
        games.add(new Game("Soccer",R.drawable.default_game));
        games.add(new Game("Tennis",R.drawable.default_game));
        games.add(new Game("Call of Duty:WARZONE",R.drawable.default_game));
        games.add(new Game("Baseball",R.drawable.default_game));
        games.add(new Game("League of Legends",R.drawable.default_game));
        games.add(new Game("Table Tennis",R.drawable.default_game));
        games.add(new Game("NBA 2K21",R.drawable.default_game));
        games.add(new Game("Fortnite",R.drawable.default_game));
    }
}