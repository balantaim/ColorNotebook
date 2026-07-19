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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.martinatanasov.colornotebook.services.AlarmReceiverService;

import java.util.Calendar;

public class AlarmEvent implements AlarmItems {

    private AlarmManager alarmManager;
    private final Context context;

    public AlarmEvent(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void setUpAlarm(String id, String title, String node, Calendar calendar, int priority, int color) {
        int requestCode = Integer.parseInt(id);
        Intent intent = new Intent(context, AlarmReceiverService.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("node", node);
        intent.putExtra("priority", priority);
        intent.putExtra("color", color);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        if (alarmManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Fallback to inexact if permission not granted
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                return;
            }
        }

        if (priority == 0) { // Important
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent);
            }
        } else if (priority == 1) { // Regular
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);
        } else { // Unimportant
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);
        }
    }

    @Override
    public void cancelAlarm(String id) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        int requestCode = Integer.parseInt(id);
        Intent intent = new Intent(context, AlarmReceiverService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("ALARM", "cancelAlarm: " + id);
        }
    }

    @Override
    public void cancelAllAlarms() {
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                alarmManager.cancelAll();
                Log.d("ALARM", "cancelAllAlarms: true");
            }
        }
    }

    public long nextAlarmTriggerTime() {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        if (alarmManager != null) {
            AlarmManager.AlarmClockInfo result = alarmManager.getNextAlarmClock();
            if (result != null) {
                return result.getTriggerTime();
            }
        }
        return 0;
    }

}
