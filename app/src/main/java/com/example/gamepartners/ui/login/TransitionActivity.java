package com.example.gamepartners.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.gamepartners.R;

public class TransitionActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 3500;
    Animation logoAnimation;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        //Animations
        logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        image = findViewById(R.id.imageViewLogo);
        image.setAnimation(logoAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(TransitionActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);

    }
}