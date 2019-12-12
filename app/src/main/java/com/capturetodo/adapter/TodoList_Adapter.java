package com.capturetodo.adapter;

import android.content.Context;
import android.text.format.DateUtils;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

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

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final ToDo_Model toDo_model = toDo_models.get(position);
        String imageUrl;

        holder.todoTitle.setText(toDo_model.getTitle());
        holder.todoDescription.setText(toDo_model.getDescription());
        holder.name.setText(toDo_model.getFullName());

        imageUrl = toDo_model.getImgUrl();
        String timeStamp = (String) DateUtils.getRelativeTimeSpanString(toDo_model.getTimestamp().getSeconds() * 1000);

        holder.dateAdded.setText(timeStamp);

        Picasso.get().load(imageUrl).placeholder(R.drawable.coloss).fit().into(holder.imageView);

        holder.doneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAt(position);
                holder.itemView.setVisibility(View.GONE);


                collectionReference.document(toDo_model.getDocPath()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "You Field Remove", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to remove data", Toast.LENGTH_SHORT).show();
                    }
                });

                imagePathRemove = toDo_model.getImgUrl();

                StorageReference imageRef = storage.getReferenceFromUrl(imagePathRemove);

                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "Image remove", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed To Remove Image", Toast.LENGTH_SHORT).show();
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

        public TextView todoTitle, todoDescription, name, dateAdded;
        public ImageView imageView, doneView;

        String userId;
        String username;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            todoTitle = itemView.findViewById(R.id.todoTitle);
            todoDescription = itemView.findViewById(R.id.todoDescription);
            dateAdded = itemView.findViewById(R.id.todoDateTV);
            name =  itemView.findViewById(R.id.todoUserNameTV);

            imageView = itemView.findViewById(R.id.todoImageView);
            doneView = itemView.findViewById(R.id.todoDone);

        }
    }

    public void removeAt(int position){
        toDo_models.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, toDo_models.size());

    }
}
