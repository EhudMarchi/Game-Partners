package com.example.gamepartners.data.model.Chat;

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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private ArrayList<Chat> chatList;
    Context context;
    int selectedItemIndex=0;

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        public TextView chatName;
        public ChatViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
        }

    }
    public ChatAdapter(Context context, ArrayList<Chat> chatList) {
        this.chatList = chatList;
        this.context = context;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater  inflater = LayoutInflater.from(context);
        View gamesView = inflater.inflate(R.layout.game_item, parent, false);
        ChatAdapter.ChatViewHolder viewHolder = new ChatAdapter.ChatViewHolder(gamesView);

        return viewHolder;
    }

    @Override
    public void onViewRecycled(@NonNull ChatViewHolder holder) {
        super.onViewRecycled(holder);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ChatViewHolder holder, final int position) {
        Chat currentChat = chatList.get(position);
//        holder.gameImage.setImageResource(currentGame.getGameImage());
//        holder.gameName.setText(currentGame.getGameName());
    }
    public Chat getSelectedChat()
    {
        return chatList.get(selectedItemIndex);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

}
