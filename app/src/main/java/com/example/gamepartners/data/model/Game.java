package com.example.gamepartners.data.model;

import android.media.Image;

import java.util.List;

public class Game {
    private String gameName;
    private int playersAmout;
    private List<ePlatform> platforms;

    enum ePlatform {
        REALITY,
        PC,
        PLAYSTATION,
        XBOX
    }
}
