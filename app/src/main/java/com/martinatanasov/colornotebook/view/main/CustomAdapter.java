package com.martinatanasov.colornotebook.view.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.model.UserEvent;
import com.martinatanasov.colornotebook.view.update.UpdateActivity;
import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private static final String TAG = "CustomAdapter";
    private List<UserEvent> userModelList;
    private List<UserEvent> userModelListFiltered;
    private Activity activity;

    public CustomAdapter(Activity activity, Context context, List<UserEvent> userModel){
        this.userModelList = userModel;
        this.userModelListFiltered = userModel;
        this.context = context;
        this.activity = activity;

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

        UserEvent userEvent = userModelList.get(position);
        holder.txtEventId.setText(userEvent.getTxtEventId());
        holder.txtEventTitle.setText(userEvent.getTxtEventTitle());
        holder.txtEventLocation.setText(userEvent.getTxtEventLocation());
        holder.txtNode.setText(userEvent.getTxtNode());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(userEvent.getTxtEventId()));
                intent.putExtra("title", (userEvent.getTxtEventTitle()));
                intent.putExtra("location", userEvent.getTxtEventLocation());
                intent.putExtra("input", userEvent.getTxtNode());
                intent.putExtra("color", userEvent.getInt_color_picker());
                intent.putExtra("avatar", userEvent.getInt_avatar_picker());
                intent.putExtra("start_year", userEvent.getInt_start_year());
                intent.putExtra("start_mouth", Integer.valueOf(userEvent.getByte_start_month()));
                intent.putExtra("start_day", Integer.valueOf(userEvent.getByte_start_day()));
                intent.putExtra("start_hour", Integer.valueOf(userEvent.getByte_start_hour()));
                intent.putExtra("start_minutes", Integer.valueOf(userEvent.getByte_start_minutes()));
                intent.putExtra("end_year", userEvent.getInt_end_year());
                intent.putExtra("end_month", Integer.valueOf(userEvent.getByte_end_month()));
                intent.putExtra("end_day", Integer.valueOf(userEvent.getByte_end_day()));
                intent.putExtra("end_hour", Integer.valueOf(userEvent.getByte_end_hour()));
                intent.putExtra("end_minutes", Integer.valueOf(userEvent.getByte_end_minutes()));
                intent.putExtra("created_date", userEvent.getLong_created_date());
                intent.putExtra("modified_date", userEvent.getLong_modified_date());
                intent.putExtra("all_day", userEvent.getInt_all_day());
                intent.putExtra("sound_notifications", userEvent.getInt_sound_notifications());
                intent.putExtra("silent_notifications", userEvent.getInt_silent_notifications());
                activity.startActivityForResult(intent, 1);
            }
        });
    }
    @Override
    public int getItemCount() {
        //return txtEventId.size();
        return userModelList.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0){
                    filterResults.values = userModelListFiltered;
                    filterResults.count = userModelListFiltered.size();
                }else{
                    String search = constraint.toString().toLowerCase();
                    List<UserEvent> userFilteredList = new ArrayList<>();
                    for(UserEvent userEvent: userModelListFiltered){
                        if(userEvent.getTxtEventTitle().toLowerCase().contains(search) ||
                                userEvent.getTxtNode().toLowerCase().contains(search) ||
                                userEvent.getTxtEventLocation().toLowerCase().contains(search)){
                            userFilteredList.add(userEvent);
                        }
                    }
                    filterResults.values = userFilteredList;
                    filterResults.count = userFilteredList.size();
                }
                return filterResults;
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userModelList = (List<UserEvent>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
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
