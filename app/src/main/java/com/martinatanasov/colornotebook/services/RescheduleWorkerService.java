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

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.martinatanasov.colornotebook.dto.UserEvent;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.utils.events.SilentNotificationWorker;

import java.util.Calendar;
import java.util.List;

public class RescheduleWorkerService extends Worker {

    private static final String TAG = "RescheduleWorker";

    public RescheduleWorkerService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Rescheduling alarms and notifications started");
        Context context = getApplicationContext();

        try (EventService eventService = new EventServiceImpl(context)) {
            List<UserEvent> events = eventService.getUserEventDto();
            AlarmEvent alarmEvent = new AlarmEvent(context);
            Calendar now = Calendar.getInstance();

            for (UserEvent event : events) {
                // Sound notifications - Reschedule regardless of time to ensure persistence on boot
                if (event.int_sound_notifications() == 1) {
                    Calendar alarmTime = getCalendarFromEvent(event);
                    alarmEvent.setUpAlarm(
                            event.txtEventId(),
                            event.txtEventTitle(),
                            event.txtNode(),
                            alarmTime,
                            event.int_avatar_picker(),
                            event.int_color_picker()
                    );
                    Log.d(TAG, "Rescheduled sound alarm for event: " + event.txtEventTitle());
                }

                // Silent notifications - only reschedule if in future
                if (event.int_silent_notifications() == 1) {
                    Calendar alarmTime = getCalendarFromEvent(event);
                    if (alarmTime.after(now)) {
                        long delay = alarmTime.getTimeInMillis() - now.getTimeInMillis();
                        SilentNotificationWorker.scheduleSilentNotification(
                                context,
                                event.txtEventId(),
                                event.txtEventTitle(),
                                event.txtNode(),
                                event.int_color_picker(),
                                event.int_avatar_picker(),
                                delay
                        );
                        Log.d(TAG, "Rescheduled silent notification for event: " + event.txtEventTitle());
                    }
                }
            }
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error rescheduling alarms/notifications", e);
            return Result.failure();
        }
    }

    private Calendar getCalendarFromEvent(UserEvent event) {
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.YEAR, event.int_start_year());
        alarmTime.set(Calendar.MONTH, event.byte_start_month());
        alarmTime.set(Calendar.DAY_OF_MONTH, event.byte_start_day());
        alarmTime.set(Calendar.HOUR_OF_DAY, event.byte_start_hour());
        alarmTime.set(Calendar.MINUTE, event.byte_start_minutes());
        alarmTime.set(Calendar.SECOND, 0);
        return alarmTime;
    }

}
