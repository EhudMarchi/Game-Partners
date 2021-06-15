package com.example.gamepartners.controller.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamepartners.R;
import com.example.gamepartners.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements Filterable {
    private ArrayList<User> users;
    private ArrayList<User> selectedUsers;
    private ArrayList<User> allUsers;
    Context context;
    int selectedItemIndex=0;
    boolean selection = false;

    public class UserViewHolder extends RecyclerView.ViewHolder{
        public ImageView userImage;
        public TextView userName, userEmail;
        public UserViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            userImage = itemView.findViewById(R.id.friend_image);
            userName = itemView.findViewById(R.id.friendName);
            userEmail = itemView.findViewById(R.id.friendEmail);
        }


    }
    public UserAdapter(Context context, ArrayList<User> usersList, boolean selection) {
        users = usersList;
        allUsers = new ArrayList<>(usersList);
        selectedUsers = new ArrayList<>();
        this.context = context;
        this.selection = selection;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View gamesView = inflater.inflate(R.layout.friend_item, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(gamesView);

        return viewHolder;
    }

    @Override
    public void onViewRecycled(@NonNull UserViewHolder holder) {
        super.onViewRecycled(holder);
    }
    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final UserViewHolder holder, final int position) {
        final User currentUser = users.get(position);
        holder.userImage.setImageResource(R.drawable.default_user);
        holder.userName.setText(currentUser.getFirstName() +" "+currentUser.getLastName());
        holder.userEmail.setText(currentUser.getEmail());

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (context != null) {
                    if (user.getValue(User.class).getUid().equals(currentUser.getUid())) {
                        User publisher = user.getValue(User.class);
                        assert publisher != null;
                        if(currentUser.getProflieImageURL()!=null) {
                            if(context!= null) {
                                try {
                                Glide.with(context).load(currentUser.getProflieImageURL()).into(holder.userImage);
                                }
                                catch (Exception e)
                                {

                                }
                            }
                        }
                        break;
                    }
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
                selectedItemIndex=position;
                if(selection)
                {
                    holder.itemView.setBackgroundColor(R.color.glowCyan);
                    if(!selectedUsers.contains(users.get(position))) {
                        selectedUsers.add(users.get(position));
                    }
                    else
                    {
                        holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                        selectedUsers.remove(users.get(position));
                    }
                }
                //view.setBackgroundColor(R.color.glowCyan);
                //notifyItemChanged(selectedItemIndex);
                //notifyDataSetChanged();
            }
        });
    }
    public User getSelectedUser()
    {
        return users.get(selectedItemIndex);
    }
    public ArrayList<User> getSelectedUsers()
    {
        return selectedUsers;
    }
    @Override
    public int getItemCount() {
        return users.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    //filtering users
    @Override
    public Filter getFilter() {
        return userFilter;
    }
    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(allUsers.size()<users.size())
            {
                allUsers = new ArrayList<>(users);
            }
            ArrayList<User> filteredList = new ArrayList<>();
            String filterPattern = constraint.toString().toLowerCase().trim();
                for (User user: allUsers) {
                    if(((user.getFirstName()+" "+user.getLastName()).toLowerCase()).contains(filterPattern))
                    {
                        filteredList.add(user);
                    }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users.clear();
            users.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
