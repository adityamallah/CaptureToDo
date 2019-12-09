package com.capturetodo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capturetodo.utils.CaptureToDoApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Post_Area extends AppCompatActivity implements View.OnClickListener {


    private static final int GALLERY_CODE = 1 ;
    //Edit Text Widgets
    private EditText title, description;

    //Image View Widgets
    private ImageView camera, upload, done, backImg;

    //Text View Widgets
    private TextView fullName, date;

    //Button Widget
    private Button submit;

    //User Details in strings
    private String uFullname;
    private String uEmailId;

    //Access Camera Here
    private String currentPhotoPath;


    //Firebase Data
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore Connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("TODOS");
    private StorageReference storageReference;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__area);
        mAuth = FirebaseAuth.getInstance();


        title = findViewById(R.id.postTitle);
        description = findViewById(R.id.postDescription);
        camera = findViewById(R.id.postCameraIcon);
        camera.setOnClickListener(this);
        upload = findViewById(R.id.postUploadIcon);
        upload.setOnClickListener(this);
        done = findViewById(R.id.postDone);
        backImg = findViewById(R.id.postImageView);
        fullName = findViewById(R.id.postUserNameTV);
        date = findViewById(R.id.postDateTV);
        submit = findViewById(R.id.postSubmitBtn);
        submit.setOnClickListener(this);

        if(CaptureToDoApi.getCaptureToDoApi() != null ){
            uFullname = CaptureToDoApi.getCaptureToDoApi().getFullName();
            uEmailId = CaptureToDoApi.getCaptureToDoApi().getEmailId();

            fullName.setText(uFullname);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){

                }else{

                }
            }
        };


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if(data != null){
                imageUri = data.getData();
                backImg.setImageURI(imageUri);
            }
        }
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Bitmap bitmapa;
            try {
                bitmapa = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                backImg.setImageBitmap(bitmapa);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth != null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.postCameraIcon:
                //get image from gallery
                dispatchTakePictureIntent();
                break;

            case R.id.postUploadIcon:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;

            case R.id.postSubmitBtn:
                submitBtnVerify();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(getBaseContext(),
                        "com.capturetodo.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, GALLERY_CODE);
            }
        }
    }

    private void submitBtnVerify(){
        String sTitle = title.getText().toString();
        String sDescription = description.getText().toString();

        if(TextUtils.isEmpty(sTitle) && TextUtils.isEmpty(sDescription)){
            title.setError("Please enter your title here");
            description.setError("Please enter your description here");
            Toast.makeText(this, "Please enter you TODO", Toast.LENGTH_SHORT).show();
            return;

        }
        if(TextUtils.isEmpty(sTitle)){
            title.setError("Please enter your title here");
            Toast.makeText(this,
                    "Please enter your title.", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(sDescription)){
            description.setError("Please enter your description here");
            Toast.makeText(this,
                    "Please enter your description.", Toast.LENGTH_LONG).show();
            return;
        }
        else {

        }

    }

}
