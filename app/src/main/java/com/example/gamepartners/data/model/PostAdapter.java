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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        final Post post = postArrayList.get(position);
        postId = post.getPostID();
        holder.username.setText(post.getPublisher().getFirstName() +" "+ post.getPublisher().getLastName());
        holder.time.setText(post.getTimePosted().toString());
        holder.subject.setText(post.getSubject());
        holder.description.setText(post.getDescription());
        holder.likes.setText(String.valueOf(post.getLikes()));
        holder.city.setText(String.valueOf(post.getCity()));
        holder.location.setText(String.valueOf(post.getLocation()));
        glide.load(post.getPublisher().getProflieImageURL()).into(holder.imgViewProfilePic);
        //glide.load(post.getGame().getGamePictureURL()).into(holder.ImgViewPostPic);

    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView username, time, likes, comments, subject, description, city, location;
        ImageView imgViewProfilePic, ImgViewPostPic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewProfilePic = (ImageView)itemView.findViewById(R.id.imgView_profilePic);
            ImgViewPostPic = (ImageView)itemView.findViewById(R.id.imgView_postpic);

            username = (TextView)itemView.findViewById(R.id.username);
            time = (TextView)itemView.findViewById(R.id.time);
            subject = (TextView)itemView.findViewById(R.id.subject);
            description = (TextView)itemView.findViewById(R.id.description);
            likes = (TextView)itemView.findViewById(R.id.likes);
            comments = (TextView)itemView.findViewById(R.id.comments);
            city = (TextView)itemView.findViewById(R.id.city);
            location = (TextView)itemView.findViewById(R.id.location);
        }
    }
}
