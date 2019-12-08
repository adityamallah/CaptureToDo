package com.capturetodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.capturetodo.utils.GifImageView;

public class Register extends AppCompatActivity {

    private TextView alreadyHaveAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        GifImageView gifImageView = findViewById(R.id.registerLoader);
        gifImageView.setGifImageResource(R.drawable.loading_anim);


        alreadyHaveAcc = findViewById(R.id.registerAlreadyHaveAccount);
        alreadyHaveAccClick();
    }

    public void alreadyHaveAccClick(){

        alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

    }
}
