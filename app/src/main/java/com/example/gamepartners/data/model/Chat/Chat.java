package com.example.gamepartners.data.model.Chat;

public class Chat {
    private String chatId;
    private String name;

    public Chat(String chatId, String name) {
        this.chatId = chatId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatId() {
        return chatId;
    }
}
