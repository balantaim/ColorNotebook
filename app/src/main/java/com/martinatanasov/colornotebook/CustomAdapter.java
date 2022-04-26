package com.martinatanasov.colornotebook;

import android.app.Activity;
import android.content.Context;
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
    private final Context context;
    private static ArrayList txtEventId, txtEventTitle, txtEventLocation, txtNode;
    Activity activity;
    //int position;

    CustomAdapter(Activity activity, Context context, ArrayList EventId, ArrayList EventTitle, ArrayList EventLocation,
                  ArrayList Node){

        this.activity=activity;
        this.context=context;
        this.txtEventId =EventId;
        this.txtEventTitle =EventTitle;
        this.txtEventLocation =EventLocation;
        this.txtNode =Node;
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

        holder.txtEventId.setText(String.valueOf(txtEventId.get(holder.getAdapterPosition())));
        holder.txtEventLocation.setText(String.valueOf(txtEventLocation.get(holder.getAdapterPosition())));
        holder.txtEventTitle.setText(String.valueOf(txtEventTitle.get(holder.getAdapterPosition())));
        holder.txtNode.setText(String.valueOf(txtNode.get(holder.getAdapterPosition())));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(txtEventId.get(holder.getAdapterPosition())));
                intent.putExtra("title", String.valueOf(txtEventLocation.get(holder.getAdapterPosition())));
                intent.putExtra("location", String.valueOf(txtEventTitle.get(holder.getAdapterPosition())));
                intent.putExtra("input", String.valueOf(txtNode.get(holder.getAdapterPosition())));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return txtEventId.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtEventId, txtEventTitle, txtEventLocation, txtNode;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEventId =itemView.findViewById(R.id.txtEventId);
            txtEventTitle =itemView.findViewById(R.id.txtEventTitle);
            txtEventLocation =itemView.findViewById(R.id.txtEventLocation);
            txtNode =itemView.findViewById(R.id.txtNode);
            mainLayout=itemView.findViewById(R.id.mainLayout);
        }
    }
}
