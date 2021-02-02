package com.example.gamepartners.data.model.Chat;

import com.example.gamepartners.data.model.FirebaseUtills;
import com.example.gamepartners.data.model.Game.Game;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Message {
    private String senderUID;
    private String senderDisplayName;
    private String text;
    private Date timeSent;
    private Message.eMessageType type;
    public enum eMessageType {
        USER_MESSAGE,
        GROUP_MESSAGE,
    }

    public Message(String senderUID, String senderDisplayName, String text, Message.eMessageType type) {
        this.senderUID = senderUID;
        if(senderDisplayName != null) {
            this.senderDisplayName = senderDisplayName;
        }
        else
        {
            this.senderDisplayName = FirebaseUtills.connedtedUser.getFirstName()+" "+FirebaseUtills.connedtedUser.getLastName();
        }
        this.text = text;
        this.type = type;
        this.timeSent = Calendar.getInstance().getTime();
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public eMessageType getType() {
        return type;
    }

    public void setType(eMessageType type) {
        this.type = type;
    }

    public Message() {
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
