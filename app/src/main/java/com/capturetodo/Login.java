package com.capturetodo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.capturetodo.utils.CaptureToDoApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;


public class Login extends AppCompatActivity {

    // User Email and Password verification Here>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

    //lottie animation loader
    LottieAnimationView animationLoader;

    //Text view widgets
    private TextView dontHaveACC, forgotPassword;

    // Edit Text widgets
    private EditText email, password;

    //Button widgets
    private Button loginBtn;

    //Firebase Data Here instance
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    private String TAG = "Checker";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        //Edit Text findViewById
        email = findViewById(R.id.loginEmailId);
        password = findViewById(R.id.loginPassword);

        //Button findViewById
        loginBtn = findViewById(R.id.loginBtn);

        //Lottie Animation Loader
        animationLoader = findViewById(R.id.loginLoader);

        //Text view findViewById
        dontHaveACC = findViewById(R.id.loginDontHaveAccount);
        dontHaveAccCLICK();

        forgotPassword = findViewById(R.id.loginForgotPassword);
        forgotPassOnClick();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtnClick();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
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
                            Intent i = new Intent(Login.this, Timeline.class);
                            startActivity (i);
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                }
            });

//            Intent i = new Intent(Login.this, Timeline.class);
//            startActivity(i);
//            finish();
//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void forgotPassOnClick(){
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Forgot_Password.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void dontHaveAccCLICK() {
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

    private void loginBtnClick() {

        loginBtn.setEnabled(false);
        String lEmail = email.getText().toString().trim();
        String lPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(lEmail) || TextUtils.isEmpty(lPassword)) {

            if (TextUtils.isEmpty(lEmail)) {
                email.setError("Please enter your email id.");
                loginBtn.setEnabled(true);
            }

            if (TextUtils.isEmpty(lPassword)) {
                password.setError("Please enter your password.");
                loginBtn.setEnabled(true);
            }

            Toast.makeText(this, "Please fill above details.", Toast.LENGTH_LONG).show();
        } else if (!lEmail.matches(emailPattern) || !lPassword.matches(passwordPattern)) {

            if (!lEmail.matches(emailPattern)) {
                email.setError("Please enter valid email id ");
                loginBtn.setEnabled(true);
            }
            if (!lPassword.matches(passwordPattern)) {
                password.setError("Please enter valid password");
                loginBtn.setEnabled(true);
            }

            Toast.makeText(this, "Please check your email and password", Toast.LENGTH_LONG).show();
            loginBtn.setEnabled(true);

        } else {

            animationLoader.setVisibility(View.VISIBLE);

            boolean handler = new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    String lEmail = email.getText().toString().trim();
                    String lPassword = password.getText().toString().trim();

                    final FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null) {
                        animationLoader.setVisibility(View.INVISIBLE);
                        Toast.makeText(Login.this, "User not found / Check your email or password", Toast.LENGTH_LONG).show();
                    } else {
                        mAuth.signInWithEmailAndPassword(lEmail, lPassword)
                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "signInWithEmail:success");
                                            final FirebaseUser user = mAuth.getCurrentUser();

                                            String currentUserId = user.getUid();

                                            collectionReference.whereEqualTo("UserId", currentUserId)
                                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                            if (e == null) {

                                                            }
                                                            assert queryDocumentSnapshots != null;
                                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                                animationLoader.setVisibility(View.INVISIBLE);
                                                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                                    CaptureToDoApi captureToDoApi = CaptureToDoApi.getCaptureToDoApi();
                                                                    captureToDoApi.setFullName(snapshot.getString("FullName"));
                                                                    captureToDoApi.setUserId(snapshot.getString("UserId"));

                                                                    updateUI(user);
                                                                }

                                                            }
                                                        }
                                                    });

                                        } else {
                                            animationLoader.setVisibility(View.INVISIBLE);
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            Toast.makeText(Login.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                        }
                                    }
                                });
                    }
                }
            }, 3000);

        }


    }

    // Firebase Starting intent after all verification ..........
    private void updateUI(FirebaseUser firebaseUser) {

        if (firebaseUser != null) {
            Intent i = new Intent(Login.this, Timeline.class);
            //animationLoader.setVisibility(View.INVISIBLE);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else {

            Toast.makeText(Login.this, "User not found here", Toast.LENGTH_LONG).show();

        }

    }


}
