package com.capturetodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.capturetodo.utils.CaptureToDoApi;
import com.capturetodo.utils.GifImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    // User UI Widgets >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private TextView alreadyHaveAcc;
    private EditText rFullName, rPassword, rConfrimPassword, rEmailId;
    private Button rRegisterBtn;

    // User Email and Password verification Here>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

    //GifImageView >>>>>>>>>>>>>>>>>>>>>>>>
    private LottieAnimationView rLoader;

    //Firebase Data
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore Connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();


        //Edit Text Widgets
        rFullName = findViewById(R.id.registerFullname);
        rPassword = findViewById(R.id.registerPassword);
        rConfrimPassword = findViewById(R.id.registerConfirmPassword);
        rEmailId = findViewById(R.id.registerEmailId);

        //GifView Loader
        rLoader = findViewById(R.id.registerLoader);

        //Button Widgets
        rRegisterBtn = findViewById(R.id.registerBtn);


        //Already have account Intent to Login.class section
        alreadyHaveAcc = findViewById(R.id.registerAlreadyHaveAccount);
        alreadyHaveAccClick();

        //auth state Listner
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = mAuth.getCurrentUser();

                if(currentUser != null){
                    Toast.makeText(Register.this, "User Logged In", Toast.LENGTH_SHORT).show();
                    //user is already loggedin...
                }else{

                    //Register Button Listener

                    rRegisterBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createAccount();
                        }
                    });
                }

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);
    }

    private void alreadyHaveAccClick(){

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

    private void createAccount(){

        final String email = rEmailId.getText().toString().trim();
        String password = rPassword.getText().toString().trim();
        String conPassword = rConfrimPassword.getText().toString().trim();
        final String fullName = rFullName.getText().toString().trim();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(conPassword) || TextUtils.isEmpty(fullName)){

            if(TextUtils.isEmpty(email)){
                rEmailId.setError("Please enter your email id");
            }
            if(TextUtils.isEmpty(password)){
                rPassword.setError("Please enter your password");
            }
            if(TextUtils.isEmpty(conPassword)){
                rConfrimPassword.setError("Please confirm your password");
            }
            if(TextUtils.isEmpty(fullName)){
                rFullName.setError("Please enter your full name");
            }

            Toast.makeText(this, "Please Fill Your Details.", Toast.LENGTH_LONG).show();
            return;
        }
        else if(!email.matches(emailPattern)){
            rEmailId.setError("Invalid Email Id");
            Toast.makeText(this, "Invalid Email Id ", Toast.LENGTH_LONG).show();
            return;
        }
        else if(!password.matches(passwordPattern)){
            rPassword.setError("Password should have special character, numbers, uppercase & lowercase alphabets with no white space.");
            Toast.makeText(this,
                    "Password should have special character, numbers, uppercase" +
                            " & lowercase alphabets with no white space.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        else if(!password.matches(conPassword)){
            rPassword.setError("Password is not matching");
            rConfrimPassword.setError("Password is not matching");
            Toast.makeText(this, "Password is not matching with confirm password!", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            rLoader.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("CreateACC", "createUserWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();

                                final String currentUserId = currentUser.getUid();

                                //Create a User Map
                                final Map<String, String> userObj = new HashMap<>();
                                userObj.put("UserId", currentUserId);
                                userObj.put("FullName", fullName);
                                userObj.put("EmailId", email);


                                //Save data to our firestore

                                collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {


                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.getResult().exists()){
                                                    rLoader.setVisibility(View.INVISIBLE);

                                                    //String name = task.getResult().getString("FullName");

                                                    //API Here To Export Data To Different Activity
                                                    CaptureToDoApi captureToDoApi = CaptureToDoApi.getCaptureToDoApi();
                                                    captureToDoApi.setEmailId(email);
                                                    captureToDoApi.setFullName(fullName);
                                                    captureToDoApi.setUserId(currentUserId);

                                                    Intent intent = new Intent(Register.this, Timeline.class);
                                                    intent.putExtra("name", fullName);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("userId", currentUserId);
                                                    startActivity(intent);
                                                    finish();

                                                }else{
                                                    rLoader.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        rLoader.setVisibility(View.INVISIBLE);
                                    }
                                });

                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("CreateACC", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Register.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();

                                rLoader.setVisibility(View.INVISIBLE);
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
        }




    }
}
