package com.example.gamepartners.data.model.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamepartners.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int FRIEND_MESSAGE = 0;
    public static final int MY_MESSAGE = 1;

    private Context mContext;
    private List<Message> mChat;


    FirebaseUser user;

    public MessageAdapter( List<Message> mChat) {
        this.mChat = mChat;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater  inflater = LayoutInflater.from(context);
        View messagesView;
        if (viewType == MY_MESSAGE) {
            messagesView = inflater.inflate(R.layout.my_message_item, parent, false);
        }
        else {
            messagesView = inflater.inflate(R.layout.friend_message_item, parent, false);        }
        MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(messagesView);
        return viewHolder;
//        if (viewType == MY_MESSAGE) {
//            View view = LayoutInflater.from(mContext).inflate(R.layout.my_message_item, parent, false);
//            return new MessageAdapter.ViewHolder(view);
//        } else {
//            View view = LayoutInflater.from(mContext).inflate(R.layout.friend_message_item, parent, false);
//            return new MessageAdapter.ViewHolder(view);
//        }
    }
    public int getItemViewType(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSenderUID().equals(user.getUid())){
            return MY_MESSAGE;
        } else {
            return FRIEND_MESSAGE;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {
        final Message message = mChat.get(position);
        holder.show_message.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView show_message;
        ImageView profile_image;
        //public TextView txt_seen;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            //profile_image = itemView.findViewById(R.id.chat_image_left);
            //txt_seen = itemView.findViewById(R.id.txt_seen);

        }


    }


}
