package com.example.gamepartners.data.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.gamepartners.R;

import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private ArrayList<Game> games;
    Context context;
    public static class GameViewHolder extends RecyclerView.ViewHolder {
        public ImageView gameImage;
        public TextView gameName;

        public GameViewHolder(View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.game_image);
            gameName = itemView.findViewById(R.id.gameName);
        }
    }
    public GameAdapter(Context context, ArrayList<Game> gamesList) {
        games = gamesList;
        this.context = context;
    }
    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater  inflater = LayoutInflater.from(context);
        View gamesView = inflater.inflate(R.layout.game_item, parent, false);
        GameAdapter.GameViewHolder viewHolder = new GameAdapter.GameViewHolder(gamesView);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        Game currentGame = games.get(position);
        holder.gameImage.setImageResource(currentGame.getGameImage());
        holder.gameName.setText(currentGame.getGameName());
    }
    @Override
    public int getItemCount() {
        return games.size();
    }
}
