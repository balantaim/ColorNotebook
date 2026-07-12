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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.dto.UserEvent;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;

import java.util.Calendar;
import java.util.List;

public class MyForegroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String CHANNEL_ID = "foreground_service_id";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("Color Notebook is syncing alarms")
                .setContentTitle("Alarms active")
                .setSmallIcon(R.drawable.ic_settings)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(1001, notification);

        new Thread(() -> {
            rescheduleAlarms();
            Log.d("Service", "Alarms rescheduled, service finished task");
            stopForeground(true);
            stopSelf();
        }).start();

        return START_NOT_STICKY;
    }

    private void rescheduleAlarms() {
        try (EventService eventService = new EventServiceImpl(getApplicationContext())) {
            List<UserEvent> events = eventService.getUserEventDto();
            AlarmEvent alarmEvent = new AlarmEvent(getApplicationContext());
            Calendar now = Calendar.getInstance();

            for (UserEvent event : events) {
                if (event.int_sound_notifications() == 1) {
                    Calendar alarmTime = Calendar.getInstance();
                    alarmTime.set(Calendar.YEAR, event.int_start_year());
                    alarmTime.set(Calendar.MONTH, event.byte_start_month());
                    alarmTime.set(Calendar.DAY_OF_MONTH, event.byte_start_day());
                    alarmTime.set(Calendar.HOUR_OF_DAY, event.byte_start_hour());
                    alarmTime.set(Calendar.MINUTE, event.byte_start_minutes());
                    alarmTime.set(Calendar.SECOND, 0);

                    if (alarmTime.after(now)) {
                        alarmEvent.setUpAlarm(
                                event.txtEventId(),
                                event.txtEventTitle(),
                                event.txtNode(),
                                alarmTime,
                                event.int_avatar_picker()
                        );
                        Log.d("Service", "Rescheduled alarm for event: " + event.txtEventTitle());
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Service", "Error rescheduling alarms", e);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
