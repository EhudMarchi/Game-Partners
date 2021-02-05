package com.example.gamepartners.data.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game.Game;
import com.example.gamepartners.ui.login.CreatePostActivity;
import com.example.gamepartners.ui.login.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context context;
    String postId;
    private ArrayList<Post> postArrayList;
    RequestManager glide;
    Animation fadeInAnimation;
    Animation fadeOutAnimation;
    public MainActivity activity;

    public PostAdapter(Context context, ArrayList<Post> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
        activity = (MainActivity) context;
        glide = Glide.with(context);
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postsView = inflater.inflate(R.layout.post_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(postsView);

        return viewHolder;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.MyViewHolder holder, int position) {
        final Post post = postArrayList.get(position);
        postId = post.getPostID();
        fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fragment_fade_enter);
        fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fragment_fade_exit);
        holder.username.setText(post.getPublisher().getFirstName() + " " + post.getPublisher().getLastName());
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String postedDateString = format.format(post.getTimePosted());
        holder.timePosted.setText(postedDateString);
        holder.gameName.setText(post.getGame().getGameName());
        holder.subject.setText(post.getSubject());
        holder.description.setText(post.getDescription());
        holder.likes.setText(String.valueOf(post.getLikesCount()));
        holder.city.setText(post.getCity());
        holder.address.setText(post.getLocation());
        try {
            holder.distance.setText(new DecimalFormat("#.##").format(GamePartnerUtills.getKmFromLatLong(Float.parseFloat(activity.latitude), Float.parseFloat(activity.longitude), (float) post.getLatitude(), (float) post.getLongitude()) )+ " km");
        }
        catch (Exception e) {
            holder.distance.setVisibility(View.GONE);
        }
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (user.getValue(User.class).getEmail().equals(post.getPublisher().getEmail())) {
                        User publisher = user.getValue(User.class);
                        assert publisher != null;
                        glide.load(publisher.getProflieImageURL()).into(holder.imgViewProfilePic);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        glide.load(post.getGame().getGamePictureURL()).into(holder.ImgViewPostPic);
        if (post.getGame().getPlatforms().contains(Game.ePlatform.REALITY)) {
            holder.realityIcon.setVisibility(View.VISIBLE);
        } else {
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
        holder.isLiked = checkIfLiked(post);
        if(holder.isLiked)
        {
            holder.likeText.setText("Liked");
        }
        if(post.getPublisher().getEmail().equals(GamePartnerUtills.connedtedUser.getEmail()))
        {
            holder.like.setEnabled(false);
        }
        holder.like.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (!holder.isLiked) {
                    post.Like();
                    holder.likeText.setText("Liked");
                    holder.isLiked = true;
                } else {
                    post.Dislike();
                    holder.likeText.setText("Like");
                    holder.isLiked = false;
                }
                holder.likes.setText(String.valueOf(post.getLikesCount()));

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
        holder.timeOccurring.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (!holder.isTimeClicked) {
                    holder.timeOccurring.startAnimation(fadeInAnimation);
                    holder.timeOccurring.setForeground(null);
                    String occurringDateString = format.format(post.getTimeOccurring());
                    holder.timeOccurring.setText(occurringDateString);
                    holder.isTimeClicked = !holder.isTimeClicked;
                } else {
                    holder.timeOccurring.startAnimation(fadeOutAnimation);
                    holder.timeOccurring.setForeground(ContextCompat.getDrawable(context, R.drawable.time));
                    holder.timeOccurring.setText("");
                    holder.isTimeClicked = !holder.isTimeClicked;
                }
            }
        });
    }

    public void updateData(ArrayList<Post> posts) {
        postArrayList.clear();
        postArrayList.addAll(posts);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }
    private boolean checkIfLiked(Post post)
    {
    boolean isLiked = false;
        for (String uid:post.getLikes())
        {
            if(uid.equals(GamePartnerUtills.connedtedUser.getUid()))
            {
                isLiked = true;
                break;
            }
        }
    return isLiked;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private boolean isLiked=false, isTimeClicked=false;
        TextView username, timePosted, likes, comments, gameName, subject, description, city, address , distance, likeText, timeOccurring;
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
            timePosted = (TextView)itemView.findViewById(R.id.time);
            gameName =(TextView)itemView.findViewById(R.id.postGame);
            subject = (TextView)itemView.findViewById(R.id.subject);
            description = (TextView)itemView.findViewById(R.id.description);
            likes = (TextView)itemView.findViewById(R.id.likes);
            comments = (TextView)itemView.findViewById(R.id.comments);
            city = (TextView)itemView.findViewById(R.id.city);
            address = (TextView)itemView.findViewById(R.id.location);
            timeOccurring = (TextView)itemView.findViewById(R.id.timeOccurring);
            distance = (TextView)itemView.findViewById(R.id.distance);
        }
    }
}
