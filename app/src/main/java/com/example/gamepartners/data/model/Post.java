package com.example.gamepartners.data.model;

import android.location.Address;
import android.location.Location;

import com.example.gamepartners.data.model.Game.Game;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public  class Post implements IPostable, ILikeable {
    private String postID;
    private Date timePosted;
    private User publisher;
    private String subject;
    private String description;
    private Game game;
    private String gameName;
    private DateTime timeOccurring;
    private Address address;
    private String city;
    private List<User> participants;
    private int likes;
    private ArrayList<Comment> comments;

    public Post() {
    }

    public Post(User publisher, String gameName, int likes) {
        this.publisher = publisher;
        this.gameName = gameName;
        this.likes = likes;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public Post(User publisher, Game game, Date timePosted, String subject, String description, int likes, String city) {
        this.timePosted = timePosted;
        this.game = game;
        this.publisher = publisher;
        this.subject = subject;
        this.description = description;
        this.city = city;
        this.likes = likes;
        this.game =  game;
        comments = new ArrayList<Comment>();
    }

    public String getCity() {
        return city;
    }

    public Date getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(Date timePosted) {
        this.timePosted = timePosted;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public DateTime getTimeOccurring() {
        return timeOccurring;
    }

    public void setTimeOccurring(DateTime timeOccurring) {
        this.timeOccurring = timeOccurring;
    }

    public Address getLocation() {
        return address;
    }

    public void setLocation(Address address) {
        this.address = address;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public void Post() {

    }

    @Override
    public void Edit() {

    }

    @Override
    public void Delete() {

    }

    @Override
    public void Like() {
        this.likes++;
    }

    @Override
    public void Dislike() {
        this.likes--;
    }
}
