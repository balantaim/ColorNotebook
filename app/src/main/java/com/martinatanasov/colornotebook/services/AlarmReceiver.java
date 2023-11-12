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
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.view.custom.CustomActivity;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int id = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("id")));
        String title = intent.getStringExtra("title");
        String node = intent.getStringExtra("node");
        int priorityValue = intent.getIntExtra("priority", 0);
        String channelIdName = "sound_notifications_channel";
        if(priorityValue == 0){
            priorityValue = NotificationCompat.PRIORITY_HIGH;
            //channelIdName = "sound_notifications_channel";
        } else if (priorityValue == 1) {
            priorityValue = NotificationCompat.DEFAULT_ALL;
            //channelIdName = "sound_notifications_channel";
        } else {
            priorityValue = NotificationCompat.PRIORITY_LOW;
            //channelIdName = "sound_notifications_channel";
        }

        Intent customIntent = new Intent(context, CustomActivity.class);
        customIntent.putExtra("id", intent.getStringExtra("id"));
        customIntent.putExtra("title", title);
        customIntent.putExtra("node", node);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, customIntent, 0);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, customIntent, PendingIntent.FLAG_IMMUTABLE);
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
                .setContentIntent(pendingIntent);

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
        //notificationManagerCompat.notify(124, builder.build());
        Log.d("ALARM", "The alarm receiver started");
    }

}