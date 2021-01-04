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

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {
    Context context;
    private ArrayList<Group> groupsArrayList;
    RequestManager glide;
    public GroupsAdapter(Context context, ArrayList<Group> groupsArrayList)
    {
        this.context = context;
        this.groupsArrayList = groupsArrayList;
        glide = Glide.with(context);
    }
    @NonNull
    @Override
    public GroupsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater  inflater = LayoutInflater.from(context);
        View groupsView = inflater.inflate(R.layout.group_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(groupsView);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GroupsAdapter.MyViewHolder holder, int position) {
        final Group group = groupsArrayList.get(position);

        TextView groupNameTextView = holder.groupName;
        groupNameTextView.setText(group.getGroupName());
        glide.load(group.getGroupImage()).into(holder.imgViewGroupImage);

    }

    @Override
    public int getItemCount() {
        return groupsArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView groupName;
        ImageView imgViewGroupImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewGroupImage = (ImageView)itemView.findViewById(R.id.group_image);
            groupName = (TextView)itemView.findViewById(R.id.groupName);
        }
    }
}
