package com.example.gamepartners.data.model;

import android.location.Location;

import com.google.type.DateTime;

import java.util.List;

public abstract class Post implements IPostable {
    private DateTime timePosted;
    private String subject;
    private String description;
    private Game game;
    private DateTime timeOccurring;
    private Location location;
    private List<User> participants;
}
