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
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.martinatanasov.colornotebook.services.AlarmReceiver;

import java.util.Calendar;

public class AlarmEvent implements AlarmItems {
    private AlarmManager alarmManager;
    private final Activity activity;
    public AlarmEvent(Activity activity){
        this.activity = activity;
        alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void setUpAlarm(String id, String title, String node, Calendar calendar, int priority){
        int requestCode = Integer.parseInt(id);
        Intent intent = new Intent(activity, AlarmReceiver.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("node", node);
        intent.putExtra("priority", priority);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //Set alarm repeating
        if(priority == 0) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    pendingIntent);
        } else if (priority == 1) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_HALF_HOUR,
                    pendingIntent);
        } else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_HOUR,
                    pendingIntent);
        }
    }

    @Override
    public void cancelAlarm(String id){
        if (alarmManager == null){
            alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        }
        int requestCode = Integer.parseInt(id);
        Intent intent = new Intent(activity, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null){
            alarmManager.cancel(pendingIntent);
            Log.d("ALARM", "cancelAlarm: " + id);
        }
    }

    @Override
    public void cancelAllAlarms() {
        if (alarmManager != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                alarmManager.cancelAll();
                Log.d("ALARM", "cancelAllAlarms: true");
            }
        }
    }
    public long nextAlarmTriggerTime(){
        AlarmManager.AlarmClockInfo result = alarmManager.getNextAlarmClock();
        return result.getTriggerTime();
    }

}
