package com.capturetodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Password extends AppCompatActivity {

    private EditText semail;
    private ImageView backNav;
    private Button submitBtn;
    private TextView passwordSuc;
    private LottieAnimationView animationView;

    private FirebaseAuth auth;

    String emailPatterns = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password);
        auth = FirebaseAuth.getInstance();

        passwordSuc = findViewById(R.id.forgotPassOnSuc);

        semail = findViewById(R.id.forgotPassEmailId);

        animationView = findViewById(R.id.forgotPasswordLoader);

        //Back Nav for forgot password
        backNav = findViewById(R.id.forgotPassBack);
        backNavOnClick();

        submitBtn = findViewById(R.id.forgotPassSubmitBtn);
        submitOnClicks();

    }

    private void backNavOnClick (){
        backNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Forgot_Password.this, Login.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void submitOnClicks(){

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(semail.getText().toString())){
                    semail.setError("Please enter your email id");
                    Toast.makeText(Forgot_Password.this, "Please enter your email id", Toast.LENGTH_LONG).show();
                }else {
                    animationView.setVisibility(View.VISIBLE);
                    boolean handler = new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            auth.sendPasswordResetEmail(semail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        animationView.setVisibility(View.INVISIBLE);
                                        Log.d("ForgotPassword", "Email sent.");
                                        passwordSuc.setVisibility(View.VISIBLE);
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    animationView.setVisibility(View.INVISIBLE);
                                    semail.setError("Email not found");
                                    Toast.makeText(Forgot_Password.this, "Email not found", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }, 3000);

                }


            }
        });
    }
}
