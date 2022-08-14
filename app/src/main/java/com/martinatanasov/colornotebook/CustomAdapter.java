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
    private static ArrayList int_color_picker, int_avatar_picker, int_start_year, int_start_month, int_start_day, int_start_hour,
            int_start_minutes, int_end_year, int_end_month, int_end_day, int_end_hour, int_end_minutes,
            int_all_day, int_sound_notifications, int_silent_notifications;
    private static final String TAG = "CustomAdapter";
    Activity activity;
    //int position;

    CustomAdapter(Activity activity, Context context, ArrayList EventId, ArrayList EventTitle, ArrayList EventLocation,
                  ArrayList Node, ArrayList color_picker, ArrayList avatar_picker, ArrayList start_year, ArrayList start_month, ArrayList start_day, ArrayList start_hour,
                  ArrayList start_minutes, ArrayList end_year, ArrayList end_month, ArrayList end_day, ArrayList end_hour, ArrayList end_minutes,
                  ArrayList all_day, ArrayList sound_notifications, ArrayList silent_notifications){

        this.activity=activity;
        this.context=context;
        this.txtEventId =EventId;
        this.txtEventTitle =EventTitle;
        this.txtEventLocation =EventLocation;
        this.txtNode =Node;

        this.int_color_picker = color_picker;
        this.int_avatar_picker = avatar_picker;
        this.int_start_year = start_year;
        this.int_start_month = start_month;
        this.int_start_day = start_day;
        this.int_start_hour = start_hour;
        this.int_start_minutes = start_minutes;
        this.int_end_year = end_year;
        this.int_end_month = end_month;
        this.int_end_day = end_day;
        this.int_end_hour = end_hour;
        this.int_end_minutes = end_minutes;
        this.int_all_day = all_day;
        this.int_sound_notifications = sound_notifications;
        this.int_silent_notifications = silent_notifications;
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

        holder.txtEventId.setText(String.valueOf(txtEventId.get(holder.getBindingAdapterPosition())));
        holder.txtEventTitle.setText(String.valueOf(txtEventTitle.get(holder.getBindingAdapterPosition())));
        holder.txtEventLocation.setText(String.valueOf(txtEventLocation.get(holder.getBindingAdapterPosition())));
        holder.txtNode.setText(String.valueOf(txtNode.get(holder.getBindingAdapterPosition())));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(txtEventId.get(holder.getBindingAdapterPosition())));
                intent.putExtra("title", String.valueOf(txtEventTitle.get(holder.getBindingAdapterPosition())));
                intent.putExtra("location", String.valueOf(txtEventLocation.get(holder.getBindingAdapterPosition())));
                intent.putExtra("input", String.valueOf(txtNode.get(holder.getBindingAdapterPosition())));

                intent.putExtra("color", String.valueOf(int_color_picker.get(holder.getBindingAdapterPosition())));
                intent.putExtra("avatar", String.valueOf(int_avatar_picker.get(holder.getBindingAdapterPosition())));
                intent.putExtra("start_year", String.valueOf(int_start_year.get(holder.getBindingAdapterPosition())));
                intent.putExtra("start_mouth", String.valueOf(int_start_month.get(holder.getBindingAdapterPosition())));
                intent.putExtra("start_day", String.valueOf(int_start_day.get(holder.getBindingAdapterPosition())));
                intent.putExtra("start_hour", String.valueOf(int_start_hour.get(holder.getBindingAdapterPosition())));
                intent.putExtra("start_minutes", String.valueOf(int_start_minutes.get(holder.getBindingAdapterPosition())));
                intent.putExtra("end_year", String.valueOf(int_end_year.get(holder.getBindingAdapterPosition())));
                intent.putExtra("end_month", String.valueOf(int_end_month.get(holder.getBindingAdapterPosition())));
                intent.putExtra("end_day", String.valueOf(int_end_day.get(holder.getBindingAdapterPosition())));
                intent.putExtra("end_hour", String.valueOf(int_end_hour.get(holder.getBindingAdapterPosition())));
                intent.putExtra("end_minutes", String.valueOf(int_end_minutes.get(holder.getBindingAdapterPosition())));
                intent.putExtra("all_day", String.valueOf(int_all_day.get(holder.getBindingAdapterPosition())));
                intent.putExtra("sound_notifications", String.valueOf(int_sound_notifications.get(holder.getBindingAdapterPosition())));
                intent.putExtra("silent_notifications", String.valueOf(int_silent_notifications.get(holder.getBindingAdapterPosition())));
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
