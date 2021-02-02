package com.example.gamepartners.data.model.Chat;

import com.example.gamepartners.data.model.Game.Game;

import java.util.ArrayList;

public class Message {
    private String senderUID;
    private String senderDisplayName;
    private String text;
    private Message.eMessageType type;
    public enum eMessageType {
        USER_MESSAGE,
        GROUP_MESSAGE,
    }

    public Message(String senderUID, String senderDisplayName, String text, Message.eMessageType type) {
        this.senderUID = senderUID;
        this.senderDisplayName = senderDisplayName;
        this.text = text;
        this.type = type;
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
