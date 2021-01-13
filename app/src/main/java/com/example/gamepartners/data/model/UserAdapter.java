package com.example.gamepartners.data.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gamepartners.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements Filterable {
    private ArrayList<User> users;
    private ArrayList<User> allUsers;
    Context context;
    int selectedItemIndex=0;

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
    public UserAdapter(Context context, ArrayList<User> usersList) {
        users = usersList;
        allUsers = new ArrayList<>(usersList);
        this.context = context;
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
        User currentUser = users.get(position);
        holder.userImage.setImageResource(R.drawable.default_user);
        holder.userName.setText(currentUser.getFirstName() +" "+currentUser.getLastName());
        holder.userEmail.setText(currentUser.getEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItemIndex=position;
                //view.setBackgroundColor(R.color.glowCyan);
                notifyItemChanged(selectedItemIndex);
                //notifyDataSetChanged();
            }
        });
    }
    public User getSelectedUser()
    {
        return users.get(selectedItemIndex);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return userFilter;
    }
    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<User> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(allUsers);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User user: allUsers) {
                    if((user.getFirstName()+" "+user.getLastName()).toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(user);
                    }
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
