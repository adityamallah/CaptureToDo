package com.capturetodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Timeline extends AppCompatActivity implements View.OnClickListener {

    //ImageView widgets
    private ImageView addPost, logOut;

    //Firebase widgets
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        mAuth = FirebaseAuth.getInstance();

        //Widgets find view by id
        addPost = findViewById(R.id.timelineAdd);
        addPost.setOnClickListener(this);
        logOut = findViewById(R.id.timelineLogout);
        logOut.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timelineAdd:
                //Add Data here
                postAreaIntent();
                break;

            case R.id.timelineLogout:
                //User Logout
                userLogout();
                break;
        }
    }

    //User Logout Function
    private void userLogout(){
        mAuth.signOut();
        Intent i = new Intent(Timeline.this, Login.class);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        startActivity(i);
        finish();
    }

    //Intent to post area from timeline
    private void postAreaIntent(){
        Intent i = new Intent(Timeline.this, Post_Area.class);
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
        startActivity(i);
        finish();
    }


}
