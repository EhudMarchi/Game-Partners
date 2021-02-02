package com.example.gamepartners.data.model;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game.Game;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context context;
    String postId;
    private ArrayList<Post> postArrayList;
    RequestManager glide;
    public PostAdapter(Context context, ArrayList<Post> postArrayList)
    {
        this.context = context;
        this.postArrayList = postArrayList;
        glide = Glide.with(context);
    }
    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater  inflater = LayoutInflater.from(context);
        View postsView = inflater.inflate(R.layout.post_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(postsView);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.MyViewHolder holder, int position) {
        final Post post = postArrayList.get(position);
        postId = post.getPostID();
        holder.username.setText(post.getPublisher().getFirstName() +" "+ post.getPublisher().getLastName());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dateString = format.format(post.getTimePosted());
        holder.time.setText(dateString);
        holder.gameName.setText(post.getGame().getGameName());
        holder.subject.setText(post.getSubject());
        holder.description.setText(post.getDescription());
        holder.likes.setText(String.valueOf(post.getLikes()));
        holder.city.setText(post.getCity());
        //holder.address.setText(post.getLocation().getAddressLine(0));
        glide.load(post.getPublisher().getProflieImageURL()).into(holder.imgViewProfilePic);
        glide.load(post.getGame().getGamePictureURL()).into(holder.ImgViewPostPic);
        if(post.getGame().getPlatforms().contains(Game.ePlatform.REALITY))
        {
            holder.realityIcon.setVisibility(View.VISIBLE);
        }
        else {
            if (post.getGame().getPlatforms().contains(Game.ePlatform.PC)) {
                holder.pcIcon.setVisibility(View.VISIBLE);
            }
            if (post.getGame().getPlatforms().contains(Game.ePlatform.PLAYSTATION)) {
                holder.playstaionIcon.setVisibility(View.VISIBLE);
            }
            if (post.getGame().getPlatforms().contains(Game.ePlatform.XBOX)) {
                holder.xboxIcon.setVisibility(View.VISIBLE);
            }
        }
        holder.like.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(!holder.isLiked) {
                    post.Like();
                    holder.likeText.setText("Liked");
                    holder.isLiked = true;
                }
                else
                {
                    post.Dislike();
                    holder.likeText.setText("Like");
                    holder.isLiked = false;
                }
                    holder.likes.setText(String.valueOf(post.getLikes()));

            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                holder.comment.setClickable(false);
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.comment_item);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                holder.comment.setClickable(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private boolean isLiked=false;
        TextView username, time, likes, comments, gameName, subject, description, city, address , likeText;
        ImageView imgViewProfilePic, ImgViewPostPic, realityIcon, pcIcon, playstaionIcon, xboxIcon;
        LinearLayout like , comment;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            like = (LinearLayout)itemView.findViewById(R.id.like);
            likeText = (TextView)itemView.findViewById(R.id.likeText);
            comment = (LinearLayout)itemView.findViewById(R.id.comment);
            imgViewProfilePic = (ImageView)itemView.findViewById(R.id.imgView_profilePic);
            ImgViewPostPic = (ImageView)itemView.findViewById(R.id.imgView_postpic);
            realityIcon = (ImageView)itemView.findViewById(R.id.realityIcon);
            pcIcon = (ImageView)itemView.findViewById(R.id.pcIcon);
            playstaionIcon = (ImageView)itemView.findViewById(R.id.playstationIcon);
            xboxIcon = (ImageView)itemView.findViewById(R.id.xboxIcon);
            username = (TextView)itemView.findViewById(R.id.username);
            time = (TextView)itemView.findViewById(R.id.time);
            gameName =(TextView)itemView.findViewById(R.id.postGame);
            subject = (TextView)itemView.findViewById(R.id.subject);
            description = (TextView)itemView.findViewById(R.id.description);
            likes = (TextView)itemView.findViewById(R.id.likes);
            comments = (TextView)itemView.findViewById(R.id.comments);
            city = (TextView)itemView.findViewById(R.id.city);
            address = (TextView)itemView.findViewById(R.id.location);
        }
    }
}
