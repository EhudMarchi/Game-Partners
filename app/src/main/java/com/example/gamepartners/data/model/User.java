package com.example.gamepartners.data.model;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game.Game;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone = "";
    private String facebookUsername = "";
    private ArrayList<User> following = new ArrayList<>();
    private ArrayList<User> followers = new ArrayList<>();
    private ArrayList<Group> groups = new ArrayList<>();
    private int profilePicture = R.drawable.default_user;

    public User() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public ArrayList<User> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<User> following) {
        this.following = following;
    }

    public ArrayList<User> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<User> followers) {
        this.followers = followers;
    }
}
