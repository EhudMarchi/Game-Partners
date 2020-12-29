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

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.MyViewHolder> {
    Context context;
    private ArrayList<Post> postArrayList;
    RequestManager glide;
    public ExploreAdapter(Context context, ArrayList<Post> postArrayList)
    {
        this.context = context;
        this.postArrayList = postArrayList;
        glide = Glide.with(context);
    }
    @NonNull
    @Override
    public ExploreAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater  inflater = LayoutInflater.from(context);
        View postsView = inflater.inflate(R.layout.row_post, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(postsView);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExploreAdapter.MyViewHolder holder, int position) {
        final Post post = postArrayList.get(position);

//        holder.username.setText(post.getPublisher().getFirstName()+" "+post.getPublisher().getLastName());
//        holder.time.setText(post.getTimePosted().toString());
//        holder.description.setText(post.getDescription());
//        holder.likes.setText(post.getLikes());
//        holder.comments.setText(post.getComments().size()+ " comments");
        TextView usernameTextView = holder.username;
        usernameTextView.setText(post.getPublisher().getFirstName());
        TextView timeTextView = holder.time;
        timeTextView.setText(post.getTimePosted().toString());
        TextView descriptionTextView = holder.description;
        descriptionTextView.setText(post.getDescription());
        TextView likesTextView = holder.likes;
        likesTextView.setText(String.valueOf(post.getLikes()));
        TextView commentsTextView = holder.comments;
        //commentsTextView.setText(post.getComments().size() + " comments");


//        glide.load(post.getPublisher().getProfilePicture()).into(holder.imgViewProfilePic);
//        if(post.getGame().getGameImage()==0) {
//        holder.getImgViewPostPic.setVisibility(View.GONE);
//        }
//        else {
//            holder.getImgViewPostPic.setVisibility(View.VISIBLE);
//            glide.load(post.getGame().getGameImage()).into(holder.getImgViewPostPic);
//        }

    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView username, time, likes, comments, description;
        ImageView imgViewProfilePic, getImgViewPostPic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewProfilePic = (ImageView)itemView.findViewById(R.id.imgView_profilePic);
            getImgViewPostPic = (ImageView)itemView.findViewById(R.id.imgView_postpic);

            username = (TextView)itemView.findViewById(R.id.username);
            time = (TextView)itemView.findViewById(R.id.time);
            description = (TextView)itemView.findViewById(R.id.description);
            likes = (TextView)itemView.findViewById(R.id.likes);
            comments = (TextView)itemView.findViewById(R.id.comments);
        }
    }
}
