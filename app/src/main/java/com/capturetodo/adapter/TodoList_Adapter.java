package com.capturetodo.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capturetodo.R;
import com.capturetodo.model.ToDo_Model;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TodoList_Adapter extends RecyclerView.Adapter<TodoList_Adapter.ViewHolder> {

    private Context context;
    private List<ToDo_Model> toDo_models;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ToDo_Model toDo_model = toDo_models.get(position);
        String imageUrl;

        holder.todoTitle.setText(toDo_model.getTitle());
        holder.todoDescription.setText(toDo_model.getDescription());
        holder.name.setText(toDo_model.getFullName());

        imageUrl = toDo_model.getImgUrl();
        String timeStamp = (String) DateUtils.getRelativeTimeSpanString(toDo_model.getTimestamp().getSeconds() * 1000);

        holder.dateAdded.setText(timeStamp);

        Picasso.get().load(imageUrl).placeholder(R.drawable.coloss).fit().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return toDo_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView todoTitle, todoDescription, name, dateAdded;
        public ImageView imageView;

        String userId;
        String username;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            todoTitle = itemView.findViewById(R.id.todoTitle);
            todoDescription = itemView.findViewById(R.id.todoDescription);
            dateAdded = itemView.findViewById(R.id.todoDateTV);
            name =  itemView.findViewById(R.id.todoUserNameTV);

            imageView = itemView.findViewById(R.id.todoImageView);

        }
    }
}
