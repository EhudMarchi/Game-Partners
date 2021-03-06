package com.example.gamepartners.data.model;

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
    private ArrayList<ePlatform> platforms;
    private String gamePictureURL = "";

    public Game() {

    }

    public enum ePlatform {
        REALITY,
        PC,
        PLAYSTATION,
        XBOX
    }

    public Game(String gameName, String gamePictureURL, ArrayList<ePlatform> platforms) {
        this.gameName = gameName;
        this.gamePictureURL = gamePictureURL;
        this.platforms = platforms;
    }

    public String getGamePictureURL() {
        return gamePictureURL;
    }

    public void setGamePictureURL(String gamePictureURL) {
        this.gamePictureURL = gamePictureURL;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }


    public ArrayList<ePlatform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(ArrayList<ePlatform> platforms) {
        this.platforms = platforms;
    }
}
