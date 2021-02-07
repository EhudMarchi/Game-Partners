package com.example.gamepartners.data.model.Chat;

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
import com.example.gamepartners.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int FRIEND_MESSAGE = 0;
    public static final int MY_MESSAGE = 1;
    public static final int GROUP_MESSAGE = 2;

    private List<Message> mChat;
    Context context;
    RequestManager glide;
    FirebaseUser user;

    public MessageAdapter(Context context,List<Message> mChat) {
        this.mChat = mChat;
        this.context = context;
        glide = Glide.with(context);
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
        else if(viewType == FRIEND_MESSAGE){
            messagesView = inflater.inflate(R.layout.friend_message_item, parent, false);
        }
        else
        {
            messagesView = inflater.inflate(R.layout.group_message_item, parent, false);
        }
        MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(messagesView);
        return viewHolder;
    }
    public int getItemViewType(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getType() == Message.eMessageType.USER_MESSAGE) {
            if (mChat.get(position).getSenderUID().equals(user.getUid())) {
                return MY_MESSAGE;
            } else {
                return FRIEND_MESSAGE;
            }
        }
        else
        {
            return GROUP_MESSAGE;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {
        final Message message = mChat.get(position);
        if(message.getType() == Message.eMessageType.USER_MESSAGE) {
            holder.show_message.setText(message.getText());
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String dateString = format.format(message.getTimeSent());
            holder.time_sent.setText(dateString);
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (!message.getSenderUID().equals(mAuth.getUid())) {
                holder.senderName.setText(message.getSenderDisplayName());
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users");
                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot user : snapshot.getChildren()) {
                            if (user.getValue(User.class).getUid().equals(message.getSenderUID())) {
                                User sender = user.getValue(User.class);
                                assert sender != null;
                                glide.load(sender.getProflieImageURL()).into(holder.profile_image);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        else
        {
            holder.group_message.setText(message.getText());
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView show_message, senderName ,group_message, time_sent;
        ImageView profile_image;
        //public TextView txt_seen;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            senderName = itemView.findViewById(R.id.senderName);
            profile_image = itemView.findViewById(R.id.chat_image_left);
            group_message = itemView.findViewById(R.id.group_message);
            time_sent = itemView.findViewById(R.id.message_time);
            //profile_image = itemView.findViewById(R.id.chat_image_left);
            //txt_seen = itemView.findViewById(R.id.txt_seen);

        }


    }


}
