package com.example.gamepartners.controller.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gamepartners.R;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Comment;
import com.example.gamepartners.data.model.Game;
import com.example.gamepartners.data.model.User;
import com.example.gamepartners.ui.Activities_Fragments.CreatePostActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private ArrayList<Comment> m_Comments;
    Comment m_SelectedComment = new Comment();
    Context m_Context;
    int m_SelectedItemIndex = 0;

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView commentText, senderName;
        public ImageView senderImage;

        public CommentViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            commentText = itemView.findViewById(R.id.comment_text);
            senderName = itemView.findViewById(R.id.commenterName);
            senderImage = itemView.findViewById(R.id.commenter_image);
        }
    }

    public CommentAdapter(Context context, ArrayList<Comment> commentsList) {
        m_Comments = commentsList;
        this.m_Context = context;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View gamesView = inflater.inflate(R.layout.comment_item, parent, false);
        CommentAdapter.CommentViewHolder viewHolder = new CommentAdapter.CommentViewHolder(gamesView);

        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final CommentViewHolder holder, final int position) {
        final Comment currentComment = m_Comments.get(position);
        holder.commentText.setText(currentComment.getText());
        holder.senderName.setText(currentComment.getSenderDisplayName());
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (user.getValue(User.class).getUid().equals(currentComment.getSenderUID())) {
                        User publisher = user.getValue(User.class);
                        assert publisher != null;
                        Glide.with(m_Context).load(currentComment.getSenderImageUrl()).into(holder.senderImage);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_SelectedItemIndex = position;
                m_SelectedComment = m_Comments.get(m_SelectedItemIndex);
            }
        });
    }
    public Comment getSelectedComment() {
        Log.e("game", "game: " + m_Comments.get(m_SelectedItemIndex).getText());
        return m_SelectedComment;
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
        return m_Comments.size();
    }

}
