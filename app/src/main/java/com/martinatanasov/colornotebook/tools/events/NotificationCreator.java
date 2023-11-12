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

package com.martinatanasov.colornotebook.tools.events;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.martinatanasov.colornotebook.R;

public class NotificationCreator {

    public void createNotificationChannel(Activity activity) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Create channel 0: Sound notifications
            CharSequence name0 = "Sound notifications";
            String description0 = activity.getString(R.string.channel_description_sound_notifications);
            int importance0 = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel0 = new NotificationChannel("sound_notifications_channel", name0, importance0);
            channel0.setDescription(description0);
            //Create channel 1: Regular
//            CharSequence name1 = activity.getString(R.string.set_regular);
//            String description1 = activity.getString(R.string.channel_description_regular);
//            int importance1 = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel1 = new NotificationChannel("regular", name1, importance1);
//            channel1.setDescription(description1);
            //Create channel 1: Silent notifications
            CharSequence name1 = "Silent notifications";
            String description1 = activity.getString(R.string.channel_description_silent_notifications);
            int importance1 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel1 = new NotificationChannel("silent_notifications_channel", name1, importance1);
            channel1.setDescription(description1);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel0);
            //notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel1);
        }
    }

//    public void createNotification(Activity activity) throws IllegalAccessException, InstantiationException {
//        // Create an explicit intent for an Activity in your app.
//        Intent intent = new Intent(activity, DetailsActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "important")
//                .setSmallIcon(R.drawable.ic_set_important)
//                .setContentTitle("My notification title")
//                .setContentText("This is event text")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                // Set the intent that fires when the user taps the notification.
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);
//
//        // notificationId is a unique int for each notification that you must define.
//        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            Toast.makeText(activity, "Notifications not allowed", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        notificationManager.notify(1000, builder.build());
//
//
//    }


}

