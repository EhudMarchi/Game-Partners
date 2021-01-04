package com.example.gamepartners.data.model;

import android.location.Location;

import com.example.gamepartners.data.model.Game.Game;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public  class Post implements IPostable, ILikeable {
    private Date timePosted;
    private User publisher;
    private String subject;
    private String description;
    private Game game;
    private DateTime timeOccurring;
    private Location location;
    private String city;
    private List<User> participants;
    private int likes;
    private ArrayList<Comment> comments;

    public Post( User publisher,Date timePosted, String description, int likes, String city,ArrayList<Comment> comments ) {
        this.timePosted = timePosted;
        this.publisher = publisher;
        this.description = description;
        this.likes = likes;
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    }

    @Override
    public void Dislike() {

    }
}
