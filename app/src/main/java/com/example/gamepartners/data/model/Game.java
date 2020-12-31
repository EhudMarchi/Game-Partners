package com.example.gamepartners.data.model;

import android.media.Image;

import com.example.gamepartners.R;

import java.util.List;

public class Game {
    private String gameName;
    private int playersAmout;
    private int gameImage = R.drawable.default_game;
    private List<ePlatform> platforms;

    enum ePlatform {
        REALITY,
        PC,
        PLAYSTATION,
        XBOX
    }

    public Game(String gameName, int gameImage) {
        this.gameName = gameName;
        this.gameImage = gameImage;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getPlayersAmout() {
        return playersAmout;
    }

    public void setPlayersAmout(int playersAmout) {
        this.playersAmout = playersAmout;
    }

    public int getGameImage() {
        return gameImage;
    }

    public void setGameImage(int gameImage) {
        this.gameImage = gameImage;
    }

    public List<ePlatform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<ePlatform> platforms) {
        this.platforms = platforms;
    }
}
