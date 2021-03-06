package com.example.gamepartners.controller.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
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
import com.example.gamepartners.data.model.Update;
import com.example.gamepartners.data.model.User;
import com.example.gamepartners.ui.Activities_Fragments.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
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
    boolean favouriteFilterOn = false;
    int filterDistance =-1;
    String searchString ="";
    boolean gpsEnabled;
    public PostAdapter(Context context, ArrayList<Post> postArrayList, Dialog comments) {
        this.context = context;
        this.allPostsArrayList = new ArrayList<>(postArrayList);
        this.postArrayList = postArrayList;
        this.commentsDialog = comments;
        activity = (MainActivity) context;
        glide = Glide.with(context);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
        fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fragment_fade_enter);
        fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fragment_fade_exit);
        holder.username.setText(post.getPublisher().getFirstName() + " " + post.getPublisher().getLastName());
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/20yy HH:mm");
        String postedDateString = format.format(post.getTimePosted());
        final Dialog imageView = new Dialog(context);
        imageView.setContentView(R.layout.user_image_view);
        imageView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        if(gpsEnabled) {
            holder.distance.setVisibility(View.VISIBLE);
            holder.distance.setText(new DecimalFormat("#.##").format(GamePartnerUtills.getKmFromLatLong(Float.parseFloat(String.valueOf(GamePartnerUtills.latitude)), Float.parseFloat(String.valueOf(GamePartnerUtills.longitude)), (float) post.getLatitude(), (float) post.getLongitude())) + " km");
        }
        else
        {
            holder.distance.setVisibility(View.GONE);
        }
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (user.getValue(User.class).getUid().equals(post.getPublisher().getUid())) {
                        User publisher = user.getValue(User.class);
                        assert publisher != null;
                        glide.load(publisher.getProflieImageURL()).into(holder.imgViewProfilePic);
                        if(imageView.getContext()!=null) {
                            try {
                                Glide.with(imageView.getContext()).load(publisher.getProflieImageURL()).into((ImageView) imageView.findViewById(R.id.user_pic));
                                TextView userName = imageView.findViewById(R.id.userDisplayName);
                                userName.setText(holder.username.getText().toString());
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        holder.imgViewProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgViewProfilePic.setClickable(false);
                imageView.show();
                holder.imgViewProfilePic.setClickable(true);
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
        if(post.getPublisher().getUid().equals(GamePartnerUtills.connectedUser.getUid()))
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
                    Update update = new Update(Update.eUpdateType.LIKE,GamePartnerUtills.connectedUser.getUid(),
                            GamePartnerUtills.getUserDisplayName(GamePartnerUtills.connectedUser.getUid()),post.getPublisher().getUid());
                    GamePartnerUtills.sendMessage(update,context);
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
                        Comment comment = new Comment(GamePartnerUtills.connectedUser, ((TextView)dialog.findViewById(R.id.commentText)).getText().toString());
                        if(!comment.getText().equals("")) {
                            try {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostID()).child("comments").child(String.valueOf(post.getComments().size()));
                                post.getComments().add(comment);
                                comment.Post(reference);
                                Toast.makeText(context, "Comment Added!", Toast.LENGTH_SHORT).show();
                                if (!post.getPublisher().getUid().equals(GamePartnerUtills.connectedUser.getUid())) {
                                    Update update = new Update(Update.eUpdateType.COMMENT, GamePartnerUtills.connectedUser.getUid(),
                                            GamePartnerUtills.getUserDisplayName(GamePartnerUtills.connectedUser.getUid()), post.getPublisher().getUid());
                                    GamePartnerUtills.sendMessage(update, context);
                                }
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(context, "Comment Failed", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                holder.comment.setClickable(true);
            }
        });
        if((post.getPublisher().getUid().equals(GamePartnerUtills.connectedUser.getUid()))||(isJoined(post.getPostID())))
        {
            holder.join.setEnabled(false);
            holder.joinText.setText("Joined");
            holder.join.setAlpha(0.6f);
        }
        else if(isPending(post.getPostID(),GamePartnerUtills.GetUser(post.getPublisher().getUid())))
        {
            holder.join.setEnabled(false);
            holder.joinText.setText("Pending");
            holder.join.setAlpha(0.6f);
        }
        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update joinUpdate = new Update(Update.eUpdateType.JOIN_GROUP, GamePartnerUtills.connectedUser.getUid(), GamePartnerUtills.getUserDisplayName(GamePartnerUtills.connectedUser.getUid()), post.getPublisher().getUid(), post.getSubject(),post.getPostID());
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(post.getPublisher().getUid()).child("requests").child(String.valueOf(GamePartnerUtills.GetUser(post.getPublisher().getUid()).getRequests().size()));
                GamePartnerUtills.GetUser(post.getPublisher().getUid()).getRequests().add(joinUpdate);
                mRef.setValue(joinUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show();
                        GamePartnerUtills.sendMessage(joinUpdate, context);
                    }
                });
                holder.join.setEnabled(false);
                holder.joinText.setText("Pending");
                holder.join.setAlpha(0.6f);
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

    private boolean isJoined(String i_GroupID) {
        boolean isJoined = false;
        for (String groupID:GamePartnerUtills.connectedUser.getUserGroups()) {
            if(groupID.equals(i_GroupID))
            {
                isJoined = true;
                break;
            }
        }
        return isJoined;
    }
    private boolean isPending(String i_GroupID, User publisher) {
        boolean isPending = false;
        for (Update update :publisher.getRequests()) {
            if(update.getSenderID().equals(GamePartnerUtills.connectedUser.getUid()))
            {
                isPending = true;
                break;
            }
        }
        return isPending;
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
        TextView username, timePosted, likes, comments, gameName, subject, description, city, address , distance, likeText, joinText, timeOccurring , isPrivate;
        ImageView imgViewProfilePic, ImgViewPostPic, realityIcon, pcIcon, playstaionIcon, xboxIcon;
        LinearLayout like , comment , join;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            like = (LinearLayout)itemView.findViewById(R.id.like);
            likeText = (TextView)itemView.findViewById(R.id.likeText);
            joinText = (TextView)itemView.findViewById(R.id.joinText);
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
            join = (LinearLayout)itemView.findViewById(R.id.join);
        }

    }
    public void setDistanceFilter(int distance)
    {
        this.filterDistance = distance;
        getFilter().filter("distance");
    }
    public void setFavFilter(boolean isFavouriteFilterOn)
    {
        this.favouriteFilterOn = isFavouriteFilterOn;
        getFilter().filter("favourite");
    }
    public void setSerachFilter(String search)
    {
        this.searchString= search.toLowerCase().trim();
        getFilter().filter(searchString);
    }
    private boolean isPostContainsString(Post post, String string)
    {
        boolean isContains = false;
        if (((post.getPublisher().getFirstName()+" "+post.getPublisher().getLastName()+" "+
                post.getGame().getGameName()+" "+post.getSubject()).toLowerCase()).contains(string))
        {
            isContains = true;
        }

        return  isContains;
    }
    //filtering posts
    @Override
    public Filter getFilter() {
        return postsFilter;
    }

    private Filter postsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(allPostsArrayList.size()<postArrayList.size())
            {
                allPostsArrayList = new ArrayList<>(postArrayList);
            }
            ArrayList<Post> filteredList = new ArrayList<>();
            for (Post post : allPostsArrayList) {
                if (isPostContainsString(post,searchString)&&(
                        (GamePartnerUtills.getKmFromLatLong(Float.parseFloat(String.valueOf(GamePartnerUtills.latitude)), Float.parseFloat(String.valueOf(GamePartnerUtills.longitude)), (float) post.getLatitude(), (float) post.getLongitude())<= filterDistance)
                                || filterDistance == -1))
                {
                    if(favouriteFilterOn) {
                        for (Game favGame : GamePartnerUtills.connectedUser.getFavouriteGames()) {
                            if (favGame.getGameName().equals(post.getGame().getGameName())) {
                                filteredList.add(post);
                            }
                        }
                    }
                    else
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
