package com.capturetodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.capturetodo.utils.GifImageView;

public class Login extends AppCompatActivity{

    private TextView dontHaveACC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        GifImageView gifImageView = findViewById(R.id.loginLoader);
        gifImageView.setGifImageResource(R.drawable.loading_anim);

        dontHaveACC = findViewById(R.id.loginDontHaveAccount);
        dontHaveAccCLICK();
    }

    private void dontHaveAccCLICK(){
        dontHaveACC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

    }

}
