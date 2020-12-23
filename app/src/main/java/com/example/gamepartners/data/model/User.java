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
}
