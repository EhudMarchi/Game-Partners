package com.example.gamepartners.data.model.Game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.gamepartners.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Game {
    private String gameName;
    private int playersAmout;
    private int gameImage = R.drawable.default_game;
    private ArrayList<ePlatform> platforms;
    private Bitmap gamePicture = null;
    public enum ePlatform {
        REALITY,
        PC,
        PLAYSTATION,
        XBOX
    }

    public Game(String gameName, int gameImage, ePlatform platform) {
        this.gameName = gameName;
        this.gameImage = gameImage;
        this.platforms = new ArrayList<ePlatform>();
        platforms.add(platform);
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

    public ArrayList<ePlatform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(ArrayList<ePlatform> platforms) {
        this.platforms = platforms;
    }
}
