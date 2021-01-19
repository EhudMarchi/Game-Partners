package com.example.gamepartners.data.model;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Chat.Chat;
import com.google.firebase.database.annotations.Nullable;

import java.util.List;

public class Group implements IJoinable{
    private String groupName;
    private String adminUID;
    private List<User> groupFriends;
    private Chat chat;
    private String proflieImageURL;

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

    public String getAdminUID() {
        return adminUID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getProflieImageURL() {
        return proflieImageURL;
    }

    public void setProflieImageURL(String proflieImageURL) {
        this.proflieImageURL = proflieImageURL;
    }

    public List<User> getGroupFriends() {
        return groupFriends;
    }

    public void setGroupFriends(List<User> groupFriends) {
        this.groupFriends = groupFriends;
    }
}
