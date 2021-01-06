package com.example.gamepartners.data.model.Chat;

import com.example.gamepartners.data.model.Group;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Chat {

    private String m_GroupName;
    private ArrayList<Message> messages;
    public Chat() {
    }

    public Chat(String i_GroupName) {
        this.m_GroupName = i_GroupName;
    }

    public String getSender() {
        return m_GroupName;
    }

    public void setSender(String i_GroupName) {
        this.m_GroupName = i_GroupName;
    }

}