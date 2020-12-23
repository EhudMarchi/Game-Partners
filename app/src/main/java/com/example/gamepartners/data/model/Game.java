package com.example.gamepartners.data.model;

import android.media.Image;

import java.util.List;

public class Game {
    private String gameName;
    private int players;
    private float rating;
    private List<ePlatform> platform;

    enum ePlatform {
        REALITY,
        PC,
        PS,
        XBOX
    }
}
