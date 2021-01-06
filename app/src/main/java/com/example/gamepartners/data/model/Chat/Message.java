package com.example.gamepartners.data.model.Chat;

public class Message {
    private String senderUID;
    private String senderDisplayName;
    private String text;

    public Message(String senderUID, String senderDisplayName, String text) {
        this.senderUID = senderUID;
        this.senderDisplayName = senderDisplayName;
        this.text = text;
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
