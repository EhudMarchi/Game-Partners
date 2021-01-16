package com.example.gamepartners.data.model.Game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamepartners.R;

import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> implements Filterable {
    private ArrayList<Game> games;
    private ArrayList<Game> allGames;
    Context context;
    int selectedItemIndex=0;

    public class GameViewHolder extends RecyclerView.ViewHolder{
        public ImageView gameImage ,realityIcon, pcIcon, playstaionIcon, xboxIcon;
        public TextView gameName;
        public GameViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            gameImage = itemView.findViewById(R.id.game_image);
            gameName = itemView.findViewById(R.id.gameName);
            realityIcon = itemView.findViewById(R.id.realityIcon);
            pcIcon = itemView.findViewById(R.id.pcIcon);
            playstaionIcon = itemView.findViewById(R.id.playstationIcon);
            xboxIcon = itemView.findViewById(R.id.xboxIcon);
        }


    }
    public GameAdapter(Context context, ArrayList<Game> gamesList) {
        games = gamesList;
        allGames = new ArrayList<>(gamesList);
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
    public void onViewRecycled(@NonNull GameViewHolder holder) {
        super.onViewRecycled(holder);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final GameViewHolder holder, final int position) {
        Game currentGame = games.get(position);
        holder.gameImage.setImageResource(currentGame.getGameImage());
        holder.gameName.setText(currentGame.getGameName());
        if(currentGame.getPlatforms().contains(Game.ePlatform.REALITY))
        {
            holder.realityIcon.setVisibility(View.VISIBLE);
        }
        else
        {
            if (currentGame.getPlatforms().contains(Game.ePlatform.PC)) {
                holder.pcIcon.setVisibility(View.VISIBLE);
            }
            if (currentGame.getPlatforms().contains(Game.ePlatform.PLAYSTATION)) {
                holder.playstaionIcon.setVisibility(View.VISIBLE);
            }
            if (currentGame.getPlatforms().contains(Game.ePlatform.XBOX)) {
                holder.xboxIcon.setVisibility(View.VISIBLE);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItemIndex=position;
                //view.setBackgroundColor(R.color.glowCyan);
                //notifyItemChanged(selectedItemIndex);
                //notifyDataSetChanged();
            }
        });
    }

    public Game getSelectedGame()
    {
        return games.get(selectedItemIndex);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    @Override
    public Filter getFilter() {
        return gameFilter;
    }
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
                    if(game.getGameName().toLowerCase().contains(filterPattern))
                    {
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
