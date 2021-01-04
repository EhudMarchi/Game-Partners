package com.example.gamepartners.data.model;

import com.example.gamepartners.R;

import java.util.List;

public class Group implements IJoinable{
    private String groupName;
    private int groupImage = R.drawable.default_user;
    private String adminUID;
    private List<User> groupFriends;

    public Group() {
    }

    public Group(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public void Join() {

    }

    @Override
    public void Leave() {

    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(int groupImage) {
        this.groupImage = groupImage;
    }

    public List<User> getGroupFriends() {
        return groupFriends;
    }

    public void setGroupFriends(List<User> groupFriends) {
        this.groupFriends = groupFriends;
    }
}
