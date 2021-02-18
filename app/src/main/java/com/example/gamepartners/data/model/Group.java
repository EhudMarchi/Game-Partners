package com.example.gamepartners.data.model;

import com.example.gamepartners.data.model.Interfaces.IJoinable;

import java.util.ArrayList;
import java.util.List;

public class Group implements IJoinable {
    private String groupName;
    private String adminUID;
    private List<User> groupFriends;
    private Chat chat;
    private String groupImageURL;

    public Group() {
    }

    public Group(User admin, String groupName) {
        this.groupName = groupName;
        this.adminUID = admin.getUid();
        groupFriends = new ArrayList<>();
        chat = new Chat();
    }
    @Override
    public void Join() {

    }

    @Override
    public void Leave() {

    }

    public String getAdminUID() {
        return adminUID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImageURL() {
        return groupImageURL;
    }

    public void setGroupImageURL(String groupImageURL) {
        this.groupImageURL = groupImageURL;
    }

    public List<User> getGroupFriends() {
        return groupFriends;
    }

    public void setGroupFriends(List<User> groupFriends) {
        this.groupFriends = groupFriends;
    }
}
