package com.capturetodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.widget.TextView;

import com.capturetodo.utils.CaptureToDoApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class SplashScreen extends AppCompatActivity {

    private TextView logo;
    private int splashScreenTime = 4000;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();




//        GifImageView gifImageView = findViewById(R.id.splashScreenGifLoader);
//        gifImageView.setGifImageResource(R.drawable.loading_anim);

        boolean handler = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {
                    collectionReference.whereEqualTo("UserId", currentUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            assert queryDocumentSnapshots != null;
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    CaptureToDoApi captureToDoApi = CaptureToDoApi.getCaptureToDoApi();
                                    captureToDoApi.setFullName(snapshot.getString("FullName"));
                                    captureToDoApi.setUserId(snapshot.getString("UserId"));
                                    Intent i = new Intent(SplashScreen.this, Timeline.class);
                                    startActivity (i);
                                    finish();
                                }
                            }
                        }
                    });
                }else {
                    Intent i = new Intent(SplashScreen.this, Login.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }

            }
        }, splashScreenTime);

    }

    @Override
    protected void onStart() {
        super.onStart();




}
}
