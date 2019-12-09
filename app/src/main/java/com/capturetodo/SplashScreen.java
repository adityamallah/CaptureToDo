package com.capturetodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.widget.TextView;


public class SplashScreen extends AppCompatActivity {

    private TextView logo;
    private int splashScreenTime = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


//        GifImageView gifImageView = findViewById(R.id.splashScreenGifLoader);
//        gifImageView.setGifImageResource(R.drawable.loading_anim);

        boolean handler = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, Post_Area.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }, splashScreenTime);

    }
}
