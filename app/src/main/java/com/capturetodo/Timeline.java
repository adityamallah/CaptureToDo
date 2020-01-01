package com.capturetodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capturetodo.adapter.TodoList_Adapter;
import com.capturetodo.model.ToDo_Model;
import com.capturetodo.utils.CaptureToDoApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Timeline extends AppCompatActivity implements View.OnClickListener {

    //ImageView widgets
    private ImageView addPost, logOut;

    //TextView widgets
    private TextView newUserView;

    //Firebase widgets
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("TODOS");

    private List<ToDo_Model> toDo_models;
    private RecyclerView recyclerView;
    private TodoList_Adapter adapter;


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
        newUserView = findViewById(R.id.timeLineNewUserText);

        toDo_models = new ArrayList<>();

        recyclerView = findViewById(R.id.timelineRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Timeline.this));


    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerDataInput();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //adapter.notifyDataSetChanged();
        toDo_models.clear();
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
    private void userLogout() {
        mAuth.signOut();
        Intent i = new Intent(Timeline.this, Login.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //Intent to post area from timeline
    private void postAreaIntent() {
        Intent i = new Intent(Timeline.this, Post_Area.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void recyclerDataInput() {

        collectionReference.whereEqualTo("userId", CaptureToDoApi.getCaptureToDoApi()
                .getUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        ToDo_Model toDo_model = queryDocumentSnapshot.toObject(ToDo_Model.class);
                        toDo_models.add(toDo_model);

                    }

                    adapter = new TodoList_Adapter(Timeline.this, toDo_models);

                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } else {
                    newUserView.setVisibility(View.VISIBLE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Timeline.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
