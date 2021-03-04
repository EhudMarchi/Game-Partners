package com.example.gamepartners.data.model;

import com.example.gamepartners.data.model.Interfaces.IJoinable;

import java.util.ArrayList;
import java.util.List;

public class Group implements IJoinable {
    private String groupName;
    private String groupID;
    private String adminUID;
    private ArrayList<User> groupFriends;
    private Chat chat;
    private String groupImageURL = "";

    public Group() {
    }


    public Group(String adminUID, String groupName, String groupID, String groupImageURL) {
        this.groupName = groupName;
        this.adminUID = adminUID;
        groupFriends = new ArrayList<>();
        chat = new Chat();
        this.groupID = groupID;
        this.groupImageURL=groupImageURL;
    }

    public String getGroupImageURL() {
        return groupImageURL;
    }

    public void setGroupImageURL(String groupImageURL) {
        this.groupImageURL = groupImageURL;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
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


    public List<User> getGroupFriends() {
        return groupFriends;
    }

    public void setGroupFriends(ArrayList<User> groupFriends) {
        this.groupFriends = groupFriends;
    }
}
