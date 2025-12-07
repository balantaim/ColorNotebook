/*
 * Copyright (c) 2022 Martin Atanasov. All rights reserved.
 *
 * IMPORTANT!
 * Use of .xml vector path, .svg, .png and .bmp files, as well as all brand logos,
 * is excluded from this license. Any use of these file types or logos requires
 * prior permission from the respective owner or copyright holder.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package com.martinatanasov.colornotebook.views.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.dto.UserEvent;
import com.martinatanasov.colornotebook.views.update.UpdateActivity;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements Filterable {
    private final Context context;
    //private static final String TAG = "CustomAdapter";
    private List<UserEvent> userModelList;
    private List<UserEvent> userModelListFiltered;
    private final Activity activity;

    public CustomAdapter(Activity activity, Context context, List<UserEvent> userModel) {
        this.userModelList = userModel;
        this.userModelListFiltered = userModel;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        //New version W/O recyclable items
        holder.setIsRecyclable(false);

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        UserEvent userEvent = userModelList.get(position);
        //holder.txtEventId.setText(userEvent.getTxtEventId());
        //SetUp title and length
        if (userEvent.txtEventTitle().length() >= 11) {
            holder.txtEventTitle.setText(userEvent.txtEventTitle().substring(0, 10) + "..");
        } else {
            holder.txtEventTitle.setText(userEvent.txtEventTitle());
        }
        //SetUp background color
        if (userEvent.int_color_picker() != 0) {
            holder.cardViewEvent.setCardBackgroundColor(
                    ContextCompat.getColor(context, getCurrentBackgroundColor(userEvent.int_color_picker()))
            );
        }
        //Show active reminder icon
        setUpActiveIconReminder(holder,
                userEvent.int_all_day(),
                userEvent.int_sound_notifications(),
                userEvent.int_silent_notifications());
        holder.mainLayout.setOnClickListener(v -> navigateToSelectedEvent(userEvent));
    }

    private void navigateToSelectedEvent(UserEvent userEvent) {
        Intent intent = new Intent(context, UpdateActivity.class);
        intent.putExtra("id", String.valueOf(userEvent.txtEventId()));
        intent.putExtra("title", (userEvent.txtEventTitle()));
        intent.putExtra("location", userEvent.txtEventLocation());
        intent.putExtra("input", userEvent.txtNode());
        intent.putExtra("color", userEvent.int_color_picker());
        intent.putExtra("avatar", userEvent.int_avatar_picker());
        intent.putExtra("start_year", userEvent.int_start_year());
        intent.putExtra("start_mouth", Integer.valueOf(userEvent.byte_start_month()));
        intent.putExtra("start_day", Integer.valueOf(userEvent.byte_start_day()));
        intent.putExtra("start_hour", Integer.valueOf(userEvent.byte_start_hour()));
        intent.putExtra("start_minutes", Integer.valueOf(userEvent.byte_start_minutes()));
        intent.putExtra("end_year", userEvent.int_end_year());
        intent.putExtra("end_month", Integer.valueOf(userEvent.byte_end_month()));
        intent.putExtra("end_day", Integer.valueOf(userEvent.byte_end_day()));
        intent.putExtra("end_hour", Integer.valueOf(userEvent.byte_end_hour()));
        intent.putExtra("end_minutes", Integer.valueOf(userEvent.byte_end_minutes()));
        intent.putExtra("created_date", userEvent.long_created_date());
        intent.putExtra("modified_date", userEvent.long_modified_date());
        intent.putExtra("all_day", userEvent.int_all_day());
        intent.putExtra("sound_notifications", userEvent.int_sound_notifications());
        intent.putExtra("silent_notifications", userEvent.int_silent_notifications());
        activity.startActivityForResult(intent, 1);
    }

    private void setUpActiveIconReminder(MyViewHolder holder, int allDay, int soundNotifications, int silentNotification) {
        if (allDay == 1) {
            holder.allDayIcon.setVisibility(View.VISIBLE);
        }
        if (soundNotifications == 1) {
            holder.soundNotificationsIcon.setVisibility(View.VISIBLE);
        }
        if (silentNotification == 1) {
            holder.silentNotificationIcon.setVisibility(View.VISIBLE);
        }
    }

    private int getCurrentBackgroundColor(int color) {
        return switch (color) {
            case 1 -> R.color.pick_sky_blue;
            case 2 -> R.color.pick_green;
            case 3 -> R.color.pick_yellow;
            case 4 -> R.color.pick_orange;
            case 5 -> R.color.error;
            case 6 -> R.color.pick_blue;
            case 7 -> R.color.pick_purple;
            default -> R.color.gray_new;
        };
    }

    @Override
    public int getItemCount() {
        //return txtEventId.size();
        return userModelList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.values = userModelListFiltered;
                    filterResults.count = userModelListFiltered.size();
                } else {
                    String search = constraint.toString().toLowerCase();
                    List<UserEvent> userFilteredList = new ArrayList<>();
                    for (UserEvent userEvent : userModelListFiltered) {
                        if (userEvent.txtEventTitle().toLowerCase().contains(search) ||
                                userEvent.txtNode().toLowerCase().contains(search) ||
                                userEvent.txtEventLocation().toLowerCase().contains(search)) {
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
                try {
                    if (results.count > 0) {
                        userModelList = (List<UserEvent>) results.values;
                        notifyDataSetChanged();
                    }
                } catch (ClassCastException e) {
                    Log.d("Adapter", "publishResults: ClassCastException: " + e);
                } catch (Exception e) {
                    Log.d("Adapter", "publishResults: " + e);
                }
            }
        };
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView allDayIcon, soundNotificationsIcon, silentNotificationIcon;
        TextView txtEventTitle, txtNode; //txtEventId, txtEventLocation;
        LinearLayout mainLayout;
        CardView cardViewEvent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // txtEventId = itemView.findViewById(R.id.txtEventId);
            txtEventTitle = itemView.findViewById(R.id.txtEventTitle);
            // txtEventLocation = itemView.findViewById(R.id.txtEventLocation);
            txtNode = itemView.findViewById(R.id.txtNode);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            cardViewEvent = itemView.findViewById(R.id.cardViewEvent);
            allDayIcon = itemView.findViewById(R.id.allDayIcon);
            soundNotificationsIcon = itemView.findViewById(R.id.soundNotificationsIcon);
            silentNotificationIcon = itemView.findViewById(R.id.silentNotificationIcon);
        }
    }
}
