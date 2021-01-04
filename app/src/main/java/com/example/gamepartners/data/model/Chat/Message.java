package com.example.gamepartners.data.model.Chat;

public class Message {
    private String messageId;
    private String senderId;
    private String text;

    public Message(String messageId, String senderId, String text) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.text = text;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }
}
