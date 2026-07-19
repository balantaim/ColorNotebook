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

package com.martinatanasov.colornotebook.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.views.custom.CustomActivity;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String GROUP_KEY_SOUND = "com.martinatanasov.colornotebook.SOUND_GROUP";
    private static final String channelIdName = "sound_notifications_channel";
    private static final int SUMMARY_ID = 0;

    @NonNull
    private static Intent getIntent(Context context, Intent intent, String title, String node) {
        Intent customIntent = new Intent(context, CustomActivity.class);
        customIntent.putExtra("id", intent.getStringExtra("id"));
        customIntent.putExtra("title", title);
        customIntent.putExtra("node", node);
        return customIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("id")));
        String title = intent.getStringExtra("title");
        String node = intent.getStringExtra("node");
        int priorityValue = intent.getIntExtra("priority", 0);

        createNotificationChannel(context);

        if (priorityValue == 0) {
            priorityValue = NotificationCompat.PRIORITY_HIGH;
        } else if (priorityValue == 1) {
            priorityValue = NotificationCompat.PRIORITY_DEFAULT;
        } else {
            priorityValue = NotificationCompat.PRIORITY_LOW;
        }

        //Create custom intent
        Intent customIntent = getIntent(context, intent, title, node);
        //Make the intent to start a new activity and clear the back stack
        customIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                id,
                customIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelIdName)
                .setSmallIcon(R.drawable.ic_alarm_2)
                .setContentTitle(title)
                .setContentText(node)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(priorityValue)
                .setContentIntent(pendingIntent)
                .setGroup(GROUP_KEY_SOUND);

        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context, channelIdName)
                .setSmallIcon(R.drawable.ic_alarm_2)
                .setContentTitle(context.getString(R.string.alarms_active_title))
                .setContentText(context.getString(R.string.alarms_active_content_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(GROUP_KEY_SOUND)
                .setGroupSummary(true)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, 1);

            return;
        }
        notificationManagerCompat.notify(id, builder.build());
        notificationManagerCompat.notify(SUMMARY_ID, summaryBuilder.build());
        Log.d("ALARM", "The alarm receiver started");
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.sound_notifications_name);
            String description = context.getString(R.string.sound_notifications_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelIdName, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
