package com.capturetodo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capturetodo.R;
import com.capturetodo.model.ToDo_Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.type.Date;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

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



        holder.todoTitle.setText(toDo_model.getTitle());
        holder.todoDescription.setText(toDo_model.getDescription());
        holder.name.setText(toDo_model.getFullName());

        //holder.days.setText("Days " + toDo_model.getTimerDays());
        holder.hours.setText("Hours " + toDo_model.getTimerHours());
        holder.minutes.setText("Minutes " + toDo_model.getTimerMinutes());

        Long daysNumber = Long.parseLong(toDo_model.getTimerDays()) * 86400000;
        Long hoursNumber = Long.parseLong(toDo_model.getTimerHours()) * 3600000;
        Long minutesNumber = Long.parseLong(toDo_model.getTimerMinutes()) * 60000;

        Long addedData = daysNumber + (hoursNumber + minutesNumber);

        new CountDownTimer(addedData, 1000){

            @Override

            public void onTick(long millisUntilFinished) {
                /*            converting the milliseconds into days, hours, minutes and seconds and displaying it in textviews             */

                holder.days.setText("Days " + TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))+"");

                holder.hours.setText("Hours " + ( TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)))+"");

                holder.minutes.setText("Minutes " +(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)))+"");

                //seconds.setText((TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))+"");
            }

            @Override

            public void onFinish() {
                /*            clearing all fields and displaying countdown finished message             */

                holder.days.setText("Days 0");
                holder.hours.setText("Hours 0");
                holder.minutes.setText("");
            }
        }.start();


        imageUrl = toDo_model.getImgUrl();
        String timeStamp = (String) DateUtils.getRelativeTimeSpanString(toDo_model.getTimestamp().getSeconds() * 1000);

        holder.dateAdded.setText(timeStamp);

        Picasso.get().load(imageUrl).placeholder(R.drawable.coloss).centerCrop().fit().into(holder.imageView);




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
                        },2000);

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
    }

    @Override
    public int getItemCount() {
        return toDo_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView todoTitle, todoDescription, name, dateAdded, days, hours, minutes;
        public ImageView imageView, doneView;


        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            todoTitle = itemView.findViewById(R.id.todoTitle);
            todoDescription = itemView.findViewById(R.id.todoDescription);
            dateAdded = itemView.findViewById(R.id.todoDateTV);
            name =  itemView.findViewById(R.id.todoUserNameTV);

            days = itemView.findViewById(R.id.todoTimerDays);
            hours = itemView.findViewById(R.id.todoTimerHours);
            minutes = itemView.findViewById(R.id.todoTimerMinutes);

            imageView = itemView.findViewById(R.id.todoImageView);
            doneView = itemView.findViewById(R.id.todoDone);

        }
    }

    private void removeAt(int position){
        toDo_models.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, toDo_models.size());

    }


}
