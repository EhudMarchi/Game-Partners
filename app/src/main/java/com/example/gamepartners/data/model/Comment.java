package com.example.gamepartners.data.model;

import com.example.gamepartners.data.model.Interfaces.ILikeable;
import com.example.gamepartners.data.model.Interfaces.IPostable;

public class Comment implements IPostable, ILikeable {
    private String content;
    private int upVotes;

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
