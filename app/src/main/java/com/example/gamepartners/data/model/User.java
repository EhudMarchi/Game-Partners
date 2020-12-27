package com.example.gamepartners.data.model;

import android.media.Image;

import com.example.gamepartners.data.model.Game;

import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Image profilePicture;
    private List<Float> favouriteGamesRatings;
    private List<Game> favouriteGames;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
    public User(String name, String email) {
        String [] fullName =name.split(" ");
        this.firstName = fullName[0];
        this.lastName = fullName[1];
        this.email = email;
    }
}
