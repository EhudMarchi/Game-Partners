package com.example.gamepartners.data.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.gamepartners.ui.login.ChatActivity;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {
    Context mContext;
    private ArrayList<Group> mGroups;
    RequestManager glide;
    public GroupsAdapter(Context mContext, ArrayList<Group> mGroups)
    {
        this.mContext = mContext;
        this.mGroups = mGroups;
        glide = Glide.with(mContext);
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
    public void onBindViewHolder(@NonNull GroupsAdapter.MyViewHolder holder, final int position) {
        final Group group = mGroups.get(position);
        holder.groupName.setText(group.getGroupName());
        if(group.getGroupFriends ()!= null) {
            holder.participantsAmount.setText(group.getGroupFriends().size() + " Members");
        }
        if(group.getGroupImageURL()!=null) {
            glide.load(group.getGroupImageURL()).into(holder.imgViewGroupImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyItemChanged(position);
                Intent intent =new Intent(mContext, ChatActivity.class);
                intent.putExtra("GroupName",group.getGroupName());
                intent.putExtra("AdminUID",group.getAdminUID());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView groupName , participantsAmount;
        ImageView imgViewGroupImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewGroupImage = (ImageView)itemView.findViewById(R.id.group_image);
            groupName = (TextView)itemView.findViewById(R.id.groupName);
            participantsAmount  = (TextView)itemView.findViewById(R.id.participants);
        }
    }
}
