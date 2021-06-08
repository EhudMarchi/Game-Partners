package com.example.gamepartners.data.model;

public class Update {
    eUpdateType type;
    String senderID;
    String senderDisplayName;
    String targetID;
    String requestText;

    public String getGroupID() {
        return groupID;
    }

    String groupID;

    public Update(eUpdateType type, String senderID, String senderDisplayName, String targetID, String groupName, String groupID) //for group
    {
        this.type = type;
        this.senderID = senderID;
        this.senderDisplayName = senderDisplayName;
        this.targetID = targetID;
        this.groupID = groupID;
        if(type == eUpdateType.JOIN_GROUP) {
            requestText = senderDisplayName + " wants to join \"" + groupName + "\"!";
        }
        else if(type == eUpdateType.MESSAGE)
        {
            requestText = senderDisplayName + " sent a new massage in \"" + groupName + "\"!";
        }

        else if(type == eUpdateType.MESSAGE)
        {
            requestText = senderDisplayName + " sent a new massage in \"" + groupName + "\"!";
        }
    }

    public enum eUpdateType {
        FRIEND,
        JOIN_GROUP,
        MESSAGE,
        RESPONSE,
        COMMENT,
        LIKE
    }

    public Update() {
    }

    public Update(eUpdateType type, String senderID, String senderDisplayName, String targetID, boolean isConfirmed)//for friend
    {
        this.type = type;
        this.senderID = senderID;
        this.senderDisplayName = senderDisplayName;
        this.targetID = targetID;
        if(isConfirmed) {
            this.requestText = senderDisplayName + " confirmed your request!";
        }
        else
        {
            this.requestText = senderDisplayName + " declined your request!";
        }
    }
    public Update(eUpdateType type, String senderID, String senderDisplayName, String targetID)//for friend
    {
        this.type = type;
        this.senderID = senderID;
        this.senderDisplayName = senderDisplayName;
        this.targetID = targetID;
        if(type == eUpdateType.FRIEND) {
            this.requestText = senderDisplayName + " sent you a friend request!";
        }
        else if(type == eUpdateType.LIKE)
        {
            this.requestText = senderDisplayName + " liked your post!";
        }
        else if(type == eUpdateType.COMMENT)
        {
            this.requestText = senderDisplayName + " commented on your post!";
        }
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

    public eUpdateType getType() {
        return type;
    }

    public void setType(eUpdateType type) {
        this.type = type;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
