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

package com.martinatanasov.colornotebook.utils.events;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.martinatanasov.colornotebook.R;

public class NotificationCreator {

    private static final String SOUND_NOTIFICATION_CHANNEL = "sound_notifications_channel";
    private static final String SILENT_NOTIFICATION_CHANNEL = "silent_notifications_channel";

    public void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Create channel 0: Sound notifications
            CharSequence name0 = context.getString(R.string.channel_name_sound_notifications);
            String description0 = context.getString(R.string.channel_description_sound_notifications);
            int importance0 = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel0 = new NotificationChannel(SOUND_NOTIFICATION_CHANNEL, name0, importance0);
            channel0.setDescription(description0);
            //Create channel 1: Regular
//            CharSequence name1 = activity.getString(R.string.set_regular);
//            String description1 = activity.getString(R.string.channel_description_regular);
//            int importance1 = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel1 = new NotificationChannel("regular", name1, importance1);
//            channel1.setDescription(description1);
            //Create channel 1: Silent notifications
            CharSequence name1 = context.getString(R.string.channel_name_silent_notifications);
            String description1 = context.getString(R.string.channel_description_silent_notifications);
            int importance1 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel1 = new NotificationChannel(SILENT_NOTIFICATION_CHANNEL, name1, importance1);
            channel1.setDescription(description1);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel0);
            //notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel1);
        }
    }

}

