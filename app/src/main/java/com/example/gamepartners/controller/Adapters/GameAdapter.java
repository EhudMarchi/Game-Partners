package com.example.gamepartners.controller.Adapters;

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
import com.example.gamepartners.data.model.Game;
import com.example.gamepartners.ui.Activities_Fragments.CreatePostActivity;

import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> implements Filterable {
    private ArrayList<Game> m_Games;
    private ArrayList<Game> m_AllGames;
    Game m_SelectedGame = new Game();
    Context m_Context;
    int m_SelectedItemIndex = 0;
    public CreatePostActivity m_Activity;

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
        m_Games = gamesList;
        m_AllGames = new ArrayList<>(gamesList);
        this.m_Context = context;
        m_Activity = (CreatePostActivity) context;
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
            Game currentGame = m_Games.get(position);
            Glide.with(m_Context).load(currentGame.getGamePictureURL()).into(holder.gameImage);
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
                    m_SelectedItemIndex = position;
                    m_SelectedGame = m_Games.get(m_SelectedItemIndex);
                    m_Activity.selectedGame = m_SelectedGame;
                    if (m_Activity.selectedGame != null) {
                        m_Activity.selectedGameName.setText(m_SelectedGame.getGameName());
                        Glide.with(m_Activity).load(m_SelectedGame.getGamePictureURL()).into(m_Activity.selectedGameImage);
                    }
                    Log.e("game", "game: " + m_Games.get(m_SelectedItemIndex).getGameName());
                }
            });
    }

    public Game getSelectedGame() {
        Log.e("game", "game: " + m_Games.get(m_SelectedItemIndex).getGameName());
        return m_SelectedGame;
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
        return m_Games.size();
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
                filteredList.addAll(m_AllGames);
            } else {
                for (Game game : m_AllGames) {
                    if ((game.getPlatforms().contains(Game.ePlatform.REALITY) && m_Activity.realityCheck) ||
                            (game.getPlatforms().contains(Game.ePlatform.PC) && m_Activity.pcCheck) ||
                            (game.getPlatforms().contains(Game.ePlatform.PLAYSTATION) && m_Activity.playstationCheck) ||
                            (game.getPlatforms().contains(Game.ePlatform.XBOX) && m_Activity.xboxCheck)) {
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
            m_Games.clear();
            m_Games.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
    private Filter gameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Game> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(m_AllGames);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Game game: m_AllGames) {
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
            m_Games.clear();
            m_Games.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
