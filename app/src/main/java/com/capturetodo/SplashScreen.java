package com.capturetodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.capturetodo.utils.GifImageView;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;

public class SplashScreen extends AppCompatActivity {

    private TextView logo;
    private int splashScreenTime = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        GifImageView gifImageView = findViewById(R.id.splashScreenGifLoader);
        gifImageView.setGifImageResource(R.drawable.loading_anim);

        boolean handler = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, Login.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }, splashScreenTime);

    }
}
