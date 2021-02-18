package com.example.gamepartners.controller.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Comment;
import com.example.gamepartners.data.model.Game;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Post;
import com.example.gamepartners.data.model.User;
import com.example.gamepartners.ui.Activities_Fragments.LoginActivity;
import com.example.gamepartners.ui.Activities_Fragments.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> implements Filterable {
    Context context;
    String postId;
    private ArrayList<Post> allPostsArrayList;
    private ArrayList<Post> postArrayList;
    RequestManager glide;
    Animation fadeInAnimation, fadeOutAnimation;
    Dialog commentsDialog;
    int selectedPostIndex;
    public MainActivity activity;
    public PostAdapter(Context context, ArrayList<Post> postArrayList, Dialog comments) {
        this.context = context;
        this.allPostsArrayList = postArrayList;
        this.postArrayList = postArrayList;
        this.commentsDialog = comments;
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
    public void onBindViewHolder(@NonNull final PostAdapter.MyViewHolder holder, final int position) {
        final Post post = postArrayList.get(position);
        selectedPostIndex = position;
        postId = post.getPostID();
        fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fragment_fade_enter);
        fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fragment_fade_exit);
        holder.username.setText(post.getPublisher().getFirstName() + " " + post.getPublisher().getLastName());
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/20yy HH:mm");
        String postedDateString = format.format(post.getTimePosted());
        if(post.isPrivate())
        {
            holder.isPrivate.setVisibility(View.VISIBLE);
        }
        holder.timePosted.setText(postedDateString);
        holder.gameName.setText(post.getGame().getGameName());
        holder.subject.setText(post.getSubject());
        holder.description.setText(post.getDescription());
        holder.likes.setText(String.valueOf(post.getLikes().size()));
        holder.comments.setText((post.getComments().size())+ " comments");
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
            holder.like.setAlpha(0.6f);
        }
        if(post.getPublisher().getEmail().equals(GamePartnerUtills.connectedUser.getEmail()))
        {
            holder.like.setEnabled(false);
        }
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.isLiked) {
                    post.Like();
                    holder.likeText.setText("Liked");
                    holder.like.setAlpha(0.6f);
                    holder.isLiked = true;
                } else {
                    post.Dislike();
                    holder.likeText.setText("Like");
                    holder.like.setAlpha(1f);
                    holder.isLiked = false;
                }
                holder.likes.setText(String.valueOf(post.getLikes().size()));
                notifyDataSetChanged();
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                holder.comment.setClickable(false);
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_comment_item);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button addComment = dialog.findViewById(R.id.postButton);
                addComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostID()).child("comments").child(String.valueOf(post.getComments().size()));
                        Comment comment = new Comment(GamePartnerUtills.connectedUser, ((TextView)dialog.findViewById(R.id.commentText)).getText().toString());
                        post.getComments().add(comment);
                        comment.Post(reference);
                        Toast.makeText(context, "Comment Added!", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(context, "Comment Failed", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
                holder.comment.setClickable(true);
            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post.getComments()!=null) {
                    RecyclerView commentsRecyclerView;
                    CommentAdapter commentsAdapter;
                    holder.comments.setClickable(false);
                    commentsRecyclerView = commentsDialog.findViewById(R.id.comments_recyclerView);
                    ImageButton exitButton = commentsDialog.findViewById(R.id.exit);
                    RecyclerView.LayoutManager commentsLayoutManager = new LinearLayoutManager(context);
                    commentsRecyclerView.setLayoutManager(commentsLayoutManager);
                    commentsAdapter = new CommentAdapter(context, post.getComments());
                    commentsRecyclerView.setAdapter(commentsAdapter);
                    commentsDialog.show();
                    exitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commentsDialog.dismiss();
                        }
                    });
                    holder.comments.setClickable(true);
                }
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

    public int getSelectedPostIndex() {
        return selectedPostIndex;
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
        return postArrayList.size();
    }
    private boolean checkIfLiked(Post post)
    {
    boolean isLiked = false;
        for (String uid:post.getLikes())
        {
            if(uid.equals(GamePartnerUtills.connectedUser.getUid()))
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
        TextView username, timePosted, likes, comments, gameName, subject, description, city, address , distance, likeText, timeOccurring , isPrivate;
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
            isPrivate = (TextView)itemView.findViewById(R.id.privatePost);
        }

    }

    @Override
    public Filter getFilter() {
        return distanceFilter;
    }

    private Filter distanceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Post> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(postArrayList);
            } else {
                for (Post post : postArrayList) {
                    if (GamePartnerUtills.getKmFromLatLong(Float.parseFloat(activity.latitude), Float.parseFloat(activity.longitude), (float) post.getLatitude(), (float) post.getLongitude())<= Integer.parseInt((String) constraint))
                    {
                        filteredList.add(post);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            updateData((ArrayList<Post>) results.values);
        }
    };
}
