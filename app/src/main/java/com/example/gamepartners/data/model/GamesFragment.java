package com.example.gamepartners.data.model;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game.Game;
import com.example.gamepartners.data.model.Game.GameAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamesFragment extends Fragment {
    private ArrayList<Game> games;
    private RecyclerView gamesRecyclerView;
    private GameAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    SearchView searchView;

    public GamesFragment() {
        // Required empty public constructor
    }

    public static GamesFragment newInstance(String param1, String param2) {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillGames();
        setUpGamesRecyclerView();
        setSearchFilter();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games, container, false);

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
        searchView = (SearchView)getView().findViewById(R.id.search);
        gamesRecyclerView = getView().findViewById(R.id.gamesRecyclerView);
        gamesRecyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewAdapter = new GameAdapter(getContext(),games);
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