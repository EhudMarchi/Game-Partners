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
    private ArrayList<Update> requests = new ArrayList<>();
    private String facebookUsername = "";
    private ArrayList<String> userGroups = new ArrayList<>();
    private ArrayList<Game> favouriteGames = new ArrayList<>();
    private HashMap<String,String> userFriends = new HashMap<>();
    private ArrayList<String> postsID = new ArrayList<>();
    private String proflieImageURL;

    public User() {

    }

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public ArrayList<String> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(ArrayList<String> userGroups) {
        this.userGroups = userGroups;
    }

    public ArrayList<Game> getFavouriteGames() {
        return favouriteGames;
    }

    public void setFavouriteGames(ArrayList<Game> favouriteGames) {
        this.favouriteGames = favouriteGames;
    }

    public ArrayList<String> getUserPosts() {
        return postsID;
    }

    public void setUserPosts(ArrayList<String> userPosts) {
        this.postsID = userPosts;
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

    public List<Float> getFavouriteGamesRatings() {
        return favouriteGamesRatings;
    }

    public void setFavouriteGamesRatings(List<Float> favouriteGamesRatings) {
        this.favouriteGamesRatings = favouriteGamesRatings;
    }

    public HashMap<String, String> getUserFriends() {
        return userFriends;
    }

    public void setUserFriends(HashMap<String, String> userFriends) {
        this.userFriends = userFriends;
    }

    public ArrayList<Update> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Update> requests) {
        this.requests = requests;
    }

    private List<Float> favouriteGamesRatings;

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
