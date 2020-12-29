package com.example.gamepartners.data.model;

import android.graphics.drawable.Drawable;
import android.media.Image;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game;

import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int profilePicture = R.drawable.default_user;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(int profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Float> getFavouriteGamesRatings() {
        return favouriteGamesRatings;
    }

    public void setFavouriteGamesRatings(List<Float> favouriteGamesRatings) {
        this.favouriteGamesRatings = favouriteGamesRatings;
    }

    public List<Game> getFavouriteGames() {
        return favouriteGames;
    }

    public void setFavouriteGames(List<Game> favouriteGames) {
        this.favouriteGames = favouriteGames;
    }

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
