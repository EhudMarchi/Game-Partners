package com.example.gamepartners.data.model.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamepartners.R;
import com.example.gamepartners.ui.login.ChatActivity;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private ArrayList<Message> messages;
    Context context;
    int selectedItemIndex=0;


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView senderName, text;
        public MessageViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            senderName = itemView.findViewById(R.id.sender);
            text = itemView.findViewById(R.id.message);
        }

    }
    public MessageAdapter(ChatActivity context, ArrayList<Message> messages) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater  inflater = LayoutInflater.from(context);
        View gamesView = inflater.inflate(R.layout.message_item, parent, false);
        MessageAdapter.MessageViewHolder viewHolder = new MessageAdapter.MessageViewHolder(gamesView);

        return viewHolder;
    }

    @Override
    public void onViewRecycled(@NonNull MessageViewHolder holder) {
        super.onViewRecycled(holder);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MessageViewHolder holder, final int position) {
        Message currentMessage = messages.get(position);
//        holder.gameImage.setImageResource(currentGame.getGameImage());
//        holder.gameName.setText(currentGame.getGameName());
    }
    public Message getSelectedMessage()
    {
        return messages.get(selectedItemIndex);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
