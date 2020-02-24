package com.capturetodo.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capturetodo.R;
import com.capturetodo.model.ToDo_Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.squareup.picasso.Picasso.get;

public class TodoList_Adapter extends RecyclerView.Adapter<TodoList_Adapter.ViewHolder> {

    private Context context;
    private List<ToDo_Model> toDo_models;
    private String imagePathRemove = null;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("TODOS");
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    public TodoList_Adapter(Context context, List<ToDo_Model> toDo_models) {
        this.context = context;
        this.toDo_models = toDo_models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.todolist_row, parent, false);

        return new ViewHolder(view, context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final ToDo_Model toDo_model = toDo_models.get(position);
        String imageUrl;

        //Sharing Data
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout a = (LinearLayout) inflater.inflate(R.layout.sharelayout, null);

        final TextView sdescription = a.findViewById(R.id.todoDescription);
        final TextView stitle = a.findViewById(R.id.todoTitle);
        final TextView stoDoDate = a.findViewById(R.id.todoDateTV);
        final ImageView sshare = a.findViewById(R.id.todoShare);
        final ImageView sdone = a.findViewById(R.id.todoDone);
        final TextView susername = a.findViewById(R.id.todoUserNameTV);
        final TextView sdays = a.findViewById(R.id.todoTimerDays);
        final TextView sminutes = a.findViewById(R.id.todoTimerMinutes);
        final TextView shours = a.findViewById(R.id.todoTimerHours);
        final ImageView simagessss = a.findViewById(R.id.todoImageView);


        holder.todoTitle.setText(toDo_model.getTitle());
        holder.todoDescription.setText(toDo_model.getDescription());
        holder.name.setText(toDo_model.getFullName());

        holder.days.setText("Days " + toDo_model.getTimerDays());
        holder.hours.setText("Hours " + toDo_model.getTimerHours());
        holder.minutes.setText("Minutes " + toDo_model.getTimerMinutes());

        Long daysNumber = Long.parseLong(toDo_model.getTimerDays()) * 86400000;
        Long hoursNumber = Long.parseLong(toDo_model.getTimerHours()) * 3600000;
        Long minutesNumber = Long.parseLong(toDo_model.getTimerMinutes()) * 60000;

        Long addedData = daysNumber + (hoursNumber + minutesNumber);

        Picasso.get().load(toDo_model.getImgUrl()).into(simagessss);


        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        try {
            Date main = formatter.parse(toDo_model.getMainTimer());

            assert main != null;
            long mainMillis = main.getTime() - System.currentTimeMillis();

            new CountDownTimer(mainMillis, 1000) {

                @Override

                public void onTick(long millisUntilFinished) {
                    /*            converting the milliseconds into days, hours, minutes and seconds and displaying it in textviews             */

                    holder.days.setText("Days " + TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "");

                    holder.hours.setText("Hours " + (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished))) + "");

                    holder.minutes.setText("Minutes " + (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "");

                }


                @SuppressLint("Assert")
                @Override
                public void onFinish() {
                    /*            clearing all fields and displaying countdown finished message             */

                    holder.days.setText("Days 0");
                    holder.hours.setText("Hours 0");
                    holder.minutes.setText("Minutes 0");
                }

            }.start();


        } catch (ParseException e) {
            e.printStackTrace();
        }


        imageUrl = toDo_model.getImgUrl();
        final String timeStamp = (String) DateUtils.getRelativeTimeSpanString(toDo_model.getTimestamp().getSeconds() * 1000);

        holder.dateAdded.setText(timeStamp);

        get().load(imageUrl).placeholder(R.drawable.coloss).centerCrop().fit().into(holder.imageView);


        holder.doneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.itemView.setVisibility(View.GONE);


                collectionReference.document(toDo_model.getDocPath()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Congratulations Your TODO Has Been Completed Finally ", Toast.LENGTH_LONG).show();

                        Boolean handler = new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                removeAt(position);
                            }
                        }, 2000);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed To Remove Your TODO", Toast.LENGTH_SHORT).show();
                        Log.d("TODORemove", "onFailure: Failed To Remove Your TODO");
                    }
                });


                imagePathRemove = toDo_model.getImgUrl();

                StorageReference imageRef = storage.getReferenceFromUrl(imagePathRemove);

                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //Toast.makeText(context, "Image remove", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "Failed To Remove Image", Toast.LENGTH_SHORT).show();
                        Log.d("ImageRemove", "onFailure: Failed to remove image ");
                    }
                });


            }


        });

        holder.share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Working Fine", Toast.LENGTH_SHORT).show();
                //loadView(holder.mainCard);

                if(ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                }else{
                        requestStoragePermission();
                        return;
                }

                stitle.setText(toDo_model.getTitle());
                sdescription.setText(toDo_model.getDescription());
                sshare.setVisibility(View.INVISIBLE);
                sdone.setVisibility(View.INVISIBLE);
                stoDoDate.setText(timeStamp);
                susername.setText(toDo_model.getFullName());
                sdays.setText("Days " + toDo_model.getTimerDays());
                sminutes.setText("Minutes " + toDo_model.getTimerMinutes());
                shours.setText("Hours " + toDo_model.getTimerHours());

                //Toast.makeText(context,  mm.getPath(), Toast.LENGTH_LONG).show();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(context, loadBitmapFromView(a)));
                shareIntent.setType("image/*");
                context.startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
    }


    @Override
    public int getItemCount() {
        return toDo_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView todoTitle, todoDescription, name, dateAdded, days, hours, minutes;
        private ImageView imageView, doneView, share;
        private CardView mainCard;
        private LinearLayout shareLa;


        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            todoTitle = itemView.findViewById(R.id.todoTitle);
            todoDescription = itemView.findViewById(R.id.todoDescription);
            dateAdded = itemView.findViewById(R.id.todoDateTV);
            name = itemView.findViewById(R.id.todoUserNameTV);

            days = itemView.findViewById(R.id.todoTimerDays);
            hours = itemView.findViewById(R.id.todoTimerHours);
            minutes = itemView.findViewById(R.id.todoTimerMinutes);

            imageView = itemView.findViewById(R.id.todoImageView);
            doneView = itemView.findViewById(R.id.todoDone);
            share = itemView.findViewById(R.id.todoShare);

            mainCard = itemView.findViewById(R.id.todoListCardViewMain);
            shareLa = itemView.findViewById(R.id.sharelayout);


        }
    }

    private void removeAt(int position) {
        toDo_models.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, toDo_models.size());

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                    inImage, "", "");
            return Uri.parse(path);
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }


    private Bitmap loadBitmapFromView(View v) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);

        return returnedBitmap;
    }

    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(context)
                    .setTitle("Permission needed")
                    .setMessage("To share your TODOS we need permission to store data.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }).setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }

    }

}

