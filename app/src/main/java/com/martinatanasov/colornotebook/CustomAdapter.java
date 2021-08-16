package com.martinatanasov.colornotebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context context;
    private ArrayList txtBookId, txtBookTitle, txtBookAuthor, txtPages;
    Activity activity;

    int position;


    CustomAdapter(Activity activity, Context context, ArrayList BookId, ArrayList BookTitle, ArrayList BookAuthor,
                  ArrayList Pages){

        this.activity=activity;
        this.context=context;
        this.txtBookId=BookId;
        this.txtBookTitle=BookTitle;
        this.txtBookAuthor=BookAuthor;
        this.txtPages=Pages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.txtBookId.setText(String.valueOf(txtBookId.get(position)));
        holder.txtBookAuthor.setText(String.valueOf(txtBookAuthor.get(position)));
        holder.txtBookTitle.setText(String.valueOf(txtBookTitle.get(position)));
        holder.txtPages.setText(String.valueOf(txtPages.get(position)));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(txtBookId.get(position)));
                intent.putExtra("author", String.valueOf(txtBookAuthor.get(position)));
                intent.putExtra("title", String.valueOf(txtBookTitle.get(position)));
                intent.putExtra("pages", String.valueOf(txtPages.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return txtBookId.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtBookId, txtBookTitle, txtBookAuthor, txtPages;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBookId=itemView.findViewById(R.id.txtBookId);
            txtBookTitle=itemView.findViewById(R.id.txtBookTitle);
            txtBookAuthor=itemView.findViewById(R.id.txtBookAuthor);
            txtPages=itemView.findViewById(R.id.txtPages);
            mainLayout=itemView.findViewById(R.id.mainLayout);
        }
    }
}
