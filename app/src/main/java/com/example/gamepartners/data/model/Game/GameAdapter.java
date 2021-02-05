package com.example.gamepartners.data.model.Game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamepartners.R;
import com.example.gamepartners.ui.login.CreatePostActivity;

import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> implements Filterable {
    private ArrayList<Game> games;
    private ArrayList<Game> allGames;
    Game selectedGame = new Game();
    Context context;
    boolean selection = false;
    int selectedItemIndex = 0;
    public CreatePostActivity activity;

    public class GameViewHolder extends RecyclerView.ViewHolder {
        public ImageView gameImage, realityIcon, pcIcon, playstationIcon, xboxIcon;
        public TextView gameName;

        public GameViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            gameImage = itemView.findViewById(R.id.game_image);
            gameName = itemView.findViewById(R.id.gameName);
            realityIcon = itemView.findViewById(R.id.realityIcon);
            pcIcon = itemView.findViewById(R.id.pcIcon);
            playstationIcon = itemView.findViewById(R.id.playstationIcon);
            xboxIcon = itemView.findViewById(R.id.xboxIcon);
        }
    }

    public GameAdapter(Context context, ArrayList<Game> gamesList) {
        games = gamesList;
        allGames = new ArrayList<>(gamesList);
        this.context = context;
        activity = (CreatePostActivity) context;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View gamesView = inflater.inflate(R.layout.game_item, parent, false);
        GameAdapter.GameViewHolder viewHolder = new GameAdapter.GameViewHolder(gamesView);

        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final GameViewHolder holder, final int position) {
            Game currentGame = games.get(position);
            Glide.with(context).load(currentGame.getGamePictureURL()).into(holder.gameImage);
            holder.gameName.setText(currentGame.getGameName());
            if (currentGame.getPlatforms().contains(Game.ePlatform.REALITY)) {
                holder.realityIcon.setVisibility(View.VISIBLE);
            } else {
                if (currentGame.getPlatforms().contains(Game.ePlatform.PC)) {
                    holder.pcIcon.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.pcIcon.setVisibility(View.GONE);
                }
                if (currentGame.getPlatforms().contains(Game.ePlatform.PLAYSTATION)) {
                    holder.playstationIcon.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.playstationIcon.setVisibility(View.GONE);
                }
                if (currentGame.getPlatforms().contains(Game.ePlatform.XBOX)) {
                    holder.xboxIcon.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.xboxIcon.setVisibility(View.GONE);
                }
                holder.realityIcon.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItemIndex = position;
                    selectedGame = games.get(selectedItemIndex);
                    activity.selectedGame = selectedGame;
                    if (activity.selectedGame != null) {
                        activity.selectedGameName.setText(selectedGame.getGameName());
                        Glide.with(activity).load(selectedGame.getGamePictureURL()).into(activity.selectedGameImage);
                    }
                    Log.e("game", "game: " + games.get(selectedItemIndex).getGameName());
                }
            });
    }

    public Game getSelectedGame() {
        Log.e("game", "game: " + games.get(selectedItemIndex).getGameName());
        return selectedGame;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return games.size();
    }

    @Override
    public Filter getFilter() {
        return platformFilter;
    }
    public Filter getFilter(String type) {
        Filter filter = null;
        if(type.equals("name"))
        {
            filter = gameFilter;
        }
        else
        {
            filter = platformFilter;
        }
        return filter;
    }

    private Filter platformFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Game> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allGames);
            } else {
                for (Game game : allGames) {
                    if ((game.getPlatforms().contains(Game.ePlatform.REALITY) && activity.realityCheck) ||
                            (game.getPlatforms().contains(Game.ePlatform.PC) && activity.pcCheck) ||
                            (game.getPlatforms().contains(Game.ePlatform.PLAYSTATION) && activity.playstationCheck) ||
                            (game.getPlatforms().contains(Game.ePlatform.XBOX) && activity.xboxCheck)) {
                        filteredList.add(game);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            games.clear();
            games.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
    private Filter gameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Game> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(allGames);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Game game: allGames) {
                            if (game.getGameName().toLowerCase().contains(filterPattern)) {
                                filteredList.add(game);
                            }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            games.clear();
            games.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
