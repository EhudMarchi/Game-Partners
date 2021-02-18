package com.example.gamepartners.data.model;

import android.location.Address;

import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Interfaces.ILikeable;
import com.example.gamepartners.data.model.Interfaces.IPostable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private boolean isPrivate = false;
    private String gameName;
    private Date timeOccurring;
    private String address;
    private double latitude, longitude;
    private String city;
    private List<User> participants;
    private ArrayList<String> likes;
    private ArrayList<Comment> comments;

    public Post() {
    }

    public Post(User publisher, String gameName) {
        this.publisher = publisher;
        this.gameName = gameName;
        this.likes = new ArrayList<>();
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public Post(String postID, User publisher, Game game, Date timePosted, String subject, String description, Address address, Date timeOccurring, boolean isPrivate) {
        this.postID = postID;
        this.timePosted = timePosted;
        this.game = game;
        this.publisher = publisher;
        this.isPrivate = isPrivate;
        this.subject = subject;
        this.description = description;
        this.city = address.getLocality()+","+address.getCountryName();
        this.address = address.getAddressLine(0).split(",")[0];
        this.likes = new ArrayList<>();
        likes.add(GamePartnerUtills.connectedUser.getUid());
        this.game =  game;
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        this.timeOccurring = timeOccurring;
        comments = new ArrayList<Comment>();
        comments.add(new Comment(publisher, description));
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public Date getTimeOccurring() {
        return timeOccurring;
    }

    public void setTimeOccurring(Date timeOccurring) {
        this.timeOccurring = timeOccurring;
    }

    public String getLocation() {
        return address;
    }

    public void setLocation(String address) {
        this.address = address;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
    @Override
    public void Post(DatabaseReference reference) {

    }

    @Override
    public void Edit() {

    }

    @Override
    public void Delete() {

    }

    @Override
    public void Like() {
        this.likes.add(GamePartnerUtills.connectedUser.getUid());
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("posts").child(this.postID).child("likes");
        myRef.setValue(this.likes);
    }

    @Override
    public void Dislike() {
        this.likes.remove(GamePartnerUtills.connectedUser.getUid());
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("posts").child(this.postID).child("likes");
        myRef.setValue(this.likes);
    }

}
