package com.martinatanasov.colornotebook.view.main;

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

import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.view.update.UpdateActivity;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private final Context context;
//    private final Context context;
    private static ArrayList<String> txtEventId, txtEventTitle, txtEventLocation, txtNode;
    private static ArrayList<Integer> int_color_picker, int_avatar_picker,
            int_start_year, int_end_year,
            int_all_day, int_sound_notifications, int_silent_notifications;
    private static ArrayList<Byte> byte_start_month, byte_start_day, byte_start_hour,
            byte_start_minutes, byte_end_month, byte_end_day, byte_end_hour, byte_end_minutes;
    private static ArrayList<Long> long_created_date, long_modified_date;
    private static final String TAG = "CustomAdapter";
    Activity activity;
    //int position;

    public CustomAdapter(Activity activity, Context context, ArrayList<String> EventId, ArrayList<String> EventTitle, ArrayList<String> EventLocation,
                         ArrayList<String> Node, ArrayList<Integer> color_picker, ArrayList<Integer> avatar_picker, ArrayList<Integer> start_year,
                         ArrayList<Byte> start_month, ArrayList<Byte> start_day, ArrayList<Byte> start_hour, ArrayList<Byte> start_minutes,
                         ArrayList<Integer> end_year, ArrayList<Byte> end_month, ArrayList<Byte> end_day, ArrayList<Byte> end_hour,
                         ArrayList<Byte> end_minutes, ArrayList<Long> long_created_date, ArrayList<Long> long_modified_date,
                         ArrayList<Integer> all_day, ArrayList<Integer> sound_notifications,
                         ArrayList<Integer> silent_notifications){

        this.activity=activity;
        this.context=context;
        this.txtEventId = EventId;
        this.txtEventTitle = EventTitle;
        this.txtEventLocation = EventLocation;
        this.txtNode = Node;

        this.int_color_picker = color_picker;
        this.int_avatar_picker = avatar_picker;
        this.int_start_year = start_year;
        this.byte_start_month = start_month;
        this.byte_start_day = start_day;
        this.byte_start_hour = start_hour;
        this.byte_start_minutes = start_minutes;
        this.int_end_year = end_year;
        this.byte_end_month = end_month;
        this.byte_end_day = end_day;
        this.byte_end_hour = end_hour;
        this.byte_end_minutes = end_minutes;
        this.long_created_date = long_created_date;
        this.long_modified_date = long_modified_date;
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

                intent.putExtra("color", int_color_picker.get(holder.getBindingAdapterPosition()));
                intent.putExtra("avatar", int_avatar_picker.get(holder.getBindingAdapterPosition()));
                intent.putExtra("start_year", int_start_year.get(holder.getBindingAdapterPosition()));
                intent.putExtra("start_mouth", byte_start_month.get(holder.getBindingAdapterPosition()));
                intent.putExtra("start_day", byte_start_day.get(holder.getBindingAdapterPosition()));
                intent.putExtra("start_hour", byte_start_hour.get(holder.getBindingAdapterPosition()));
                intent.putExtra("start_minutes", byte_start_minutes.get(holder.getBindingAdapterPosition()));
                intent.putExtra("end_year", int_end_year.get(holder.getBindingAdapterPosition()));
                intent.putExtra("end_month", byte_end_month.get(holder.getBindingAdapterPosition()));
                intent.putExtra("end_day", byte_end_day.get(holder.getBindingAdapterPosition()));
                intent.putExtra("end_hour", byte_end_hour.get(holder.getBindingAdapterPosition()));
                intent.putExtra("end_minutes", byte_end_minutes.get(holder.getBindingAdapterPosition()));

                intent.putExtra("created_date", long_created_date.get(holder.getBindingAdapterPosition()));
                intent.putExtra("modified_date", long_modified_date.get(holder.getBindingAdapterPosition()));

                intent.putExtra("all_day", int_all_day.get(holder.getBindingAdapterPosition()));
                intent.putExtra("sound_notifications", int_sound_notifications.get(holder.getBindingAdapterPosition()));
                intent.putExtra("silent_notifications", int_silent_notifications.get(holder.getBindingAdapterPosition()));
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
