package com.example.gamepartners.data.model;

import java.util.List;

public class Group implements IJoinable{
    private String adminUID;
    private List<User> groupFriends;

    @Override
    public void Join() {

    }

    @Override
    public void Leave() {

    }
}
