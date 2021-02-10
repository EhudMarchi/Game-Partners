package com.example.gamepartners.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    String status;
    private String facebookUsername = "";
    private ArrayList<Group> userGroups = new ArrayList<>();
    private HashMap<String,String> userFriends = new HashMap<>();
    private ArrayList<Post> userPosts = new ArrayList<>();
    private String proflieImageURL;

    public User() {

    }

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public ArrayList<Post> getUserPosts() {
        return userPosts;
    }

    public void setUserPosts(ArrayList<Post> userPosts) {
        this.userPosts = userPosts;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public HashMap<String, String> getUserFriends() {
        return userFriends;
    }

    public void setUserFriends(HashMap<String, String> userFriends) {
        this.userFriends = userFriends;
    }

    private List<Float> favouriteGamesRatings;
    private List<Game> favouriteGames;

    public User(String firstName, String lastName, String email, String password, String proflieImageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.proflieImageURL = proflieImageURL;
    }
    public User(String firstName, String lastName, String email, String proflieImageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.proflieImageURL = proflieImageURL;
        //this.userPosts = new ArrayList<>();
    }

    public String getProflieImageURL() {
        return proflieImageURL;
    }

    public void setProflieImageURL(String proflieImageURL) {
        this.proflieImageURL = proflieImageURL;
        //FirebaseUtills.ChangeProfileImageUrl(proflieImageURL);
    }
}
