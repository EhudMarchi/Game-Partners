package com.example.gamepartners.data.model;

import com.google.firebase.database.annotations.Nullable;

public class Request {
    eRequestType type;
    String senderID;
    String senderDisplayName;
    String targetID;
    String requestText;

    public Request(eRequestType type, String senderID, String senderDisplayName, String targetID, String groupName) //for group
    {
        this.type = type;
        this.senderID = senderID;
        this.senderDisplayName = senderDisplayName;
        this.targetID = targetID;
        requestText = senderDisplayName+" wants to join to \""+groupName+"\"!";
    }

    public enum eRequestType {
        FRIEND,
        JOIN_GROUP,
    }

    public Request() {
    }

    public Request(eRequestType type, String senderID, String senderDisplayName, String targetID)//for friend
    {
        this.type = type;
        this.senderID = senderID;
        this.senderDisplayName = senderDisplayName;
        this.targetID = targetID;
        this.requestText = senderDisplayName+" sent you a friend request!";
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public eRequestType getType() {
        return type;
    }

    public void setType(eRequestType type) {
        this.type = type;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
