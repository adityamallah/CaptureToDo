package com.capturetodo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.capturetodo.model.ToDo_Model;
import com.capturetodo.utils.CaptureToDoApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Post_Area extends AppCompatActivity implements View.OnClickListener {


    private static final int GALLERY_CODE = 1 ;
    //Edit Text Widgets
    private EditText title, description, tDays, tHours, tMinutes;

    //Image View Widgets
    private ImageView camera, upload, done, backImg, backBtn;

    //Text View Widgets
    private TextView fullName, date;

    //Button Widget
    private Button submit;

    //User Details in strings
    private String uFullname;
    private String uEmailId;

    //Airbnb assets
    LottieAnimationView pLoader, pCeleSub;

    //Access Camera Here
    private String currentPhotoPath;


    //Firebase Data
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore Connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("TODOS");
    private CollectionReference collectionReferenceDocs = db.collection("Users");
    private DocumentReference dr;
    private StorageReference storageReference;
    private Uri imageUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__area);
        mAuth = FirebaseAuth.getInstance();


        storageReference = FirebaseStorage.getInstance().getReference();

        title = findViewById(R.id.postTitle);
        description = findViewById(R.id.postDescription);
        camera = findViewById(R.id.postCameraIcon);
        camera.setOnClickListener(this);
        upload = findViewById(R.id.postUploadIcon);
        upload.setOnClickListener(this);
        //done = findViewById(R.id.postDone);
        backImg = findViewById(R.id.postImageView);
        fullName = findViewById(R.id.postUserNameTV);
        date = findViewById(R.id.postDateTV);
        submit = findViewById(R.id.postSubmitBtn);
        submit.setOnClickListener(this);
        backBtn = findViewById(R.id.timelineBack);
        backBtn.setOnClickListener(this);

        tDays = findViewById(R.id.postTimerDay);
        tHours = findViewById(R.id.postTimerHours);
        tMinutes = findViewById(R.id.postTimerMinutes);

        pLoader = findViewById(R.id.postLoader);
        //pCeleSub = findViewById(R.id.postRibbonOnSub);


        if(CaptureToDoApi.getCaptureToDoApi() != null ){
            uFullname = CaptureToDoApi.getCaptureToDoApi().getFullName();
            uEmailId = CaptureToDoApi.getCaptureToDoApi().getEmailId();

            fullName.setText(uFullname);
        }


        //collectionSetDetails();

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

    /// On click for all ids HERE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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

            case R.id.timelineBack:
                // Data for intent here of back button
                backBtnIntent();
                break;
        }
    }

    //Create image file for live camera shot >>>>>>>>>>>>>>>>>>>>>>>>>
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


    // Intent that puts created image in backImage view here >>>>>>>>>>>>>>>>>>
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

        final String sTitle = title.getText().toString().trim();
        final String sDescription = description.getText().toString().trim();
        final String sTDays = tDays.getText().toString().trim();
        final String sTHours = tHours.getText().toString().trim();
        final String sTMinutes = tMinutes.getText().toString().trim();


        if(TextUtils.isEmpty(sTitle) && TextUtils.isEmpty(sDescription) && TextUtils.isEmpty(sTDays)
            && TextUtils.isEmpty(sTHours) && TextUtils.isEmpty(sTMinutes)){
            title.setError("Please enter your title here");
            description.setError("Please enter your description here");
            tDays.setError("Please set day");
            tHours.setError("Please set hour");
            tMinutes.setError("Please set minutes");
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
        if(TextUtils.isEmpty(sTDays)){
            tDays.setError("Please set day");
            Toast.makeText(this, "Please set day", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(sTHours)){
            tHours.setError("Please set hour");
            Toast.makeText(this, "Please set hour", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(sTMinutes)){
            tMinutes.setError("Please set minute");
            Toast.makeText(this, "Please set minute", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Integer.parseInt(sTDays) > 30){
            tDays.setError("Days should not be more then 30");
            Toast.makeText(this, "Days should not be more then 30", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Integer.parseInt(sTHours) > 24){
            tHours.setError("Hours should not be more then 24");
            Toast.makeText(this, "Hours should not be more then 24", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Integer.parseInt(sTMinutes) > 60){
            tMinutes.setError("Minutes should not be more then 60");
            Toast.makeText(Post_Area.this, "Minutes should not be more then 60", Toast.LENGTH_SHORT).show();
        }
        else {
            pLoader.setVisibility(View.VISIBLE);
            final StorageReference fiePath = storageReference.child("TODO_Images").child("TODOIMAGE"
                    + Timestamp.now().getSeconds());
            final String docPathUU = "TodosDocPath" + Timestamp.now().getSeconds();

            dr = collectionReference.document(docPathUU);

            fiePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {


                    fiePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imageUrls = uri.toString();
                            Timestamp timeStamp = Timestamp.now();


                            //Saving data in objects
                            final ToDo_Model toDoModel = new ToDo_Model();
                            toDoModel.setTitle(sTitle);
                            toDoModel.setDescription(sDescription);
                            toDoModel.setImgUrl(imageUrls);
                            toDoModel.setFullName(uFullname);
                            toDoModel.setDocPath(docPathUU);
                            toDoModel.setEmailId(uEmailId);
                            toDoModel.setTimestamp(timeStamp);
                            toDoModel.setUserId(currentUser.getUid());
                            toDoModel.setTimerDays(sTDays);
                            toDoModel.setTimerHours(sTHours);
                            toDoModel.setTimerMinutes(sTMinutes);

                            //Collection reference with Document reference revoke here
                            dr.set(toDoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    pLoader.setVisibility(View.INVISIBLE);
                                    Intent i = new Intent(Post_Area.this, Timeline.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pLoader.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Post_Area.this, "Failed to post your todo. Please try again", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pLoader.setVisibility(View.INVISIBLE);
                    Toast.makeText(Post_Area.this, "Failed to post your todo. Please try again", Toast.LENGTH_LONG).show();
                }
            });


        }

    }


    //Upload and retrive data from Firebase>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private void collectionSetDetails(){

        collectionReferenceDocs.whereEqualTo("UserId" , currentUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if(e == null){

                }
                assert queryDocumentSnapshots != null;
                if(!queryDocumentSnapshots.isEmpty()){

                    for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        CaptureToDoApi captureToDoApi = new CaptureToDoApi();

                        captureToDoApi.setFullName(queryDocumentSnapshot.getString("FullName"));
                        fullName.setText(captureToDoApi.getFullName());
                        Toast.makeText(captureToDoApi, queryDocumentSnapshot.getString("FullName"), Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    //Backbtn Intent function here>>>>>>>>>>>>>>>>>>>>>>>>
    private void backBtnIntent(){
        Intent i = new Intent(Post_Area.this, Timeline.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


    }

}
