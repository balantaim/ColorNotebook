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

package com.martinatanasov.colornotebook.controllers;

import android.app.Activity;
import android.util.Log;

import com.martinatanasov.colornotebook.services.EventService;
import com.martinatanasov.colornotebook.services.EventServiceImpl;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.utils.events.SilentNotificationWorker;
import com.martinatanasov.colornotebook.views.custom.CustomActivity;

public class CustomActivityController {

    private static final String TAG = "CustomActivityController";
    private final Activity view;

    public CustomActivityController(CustomActivity view) {
        this.view = view;
    }

    public void cancelCurrentAlarm(String id) {
        AlarmEvent alarm = new AlarmEvent(this.view);
        alarm.cancelAlarm(id);

        Log.d("Alarm", "onNextAlarm: " + alarm.nextAlarmTriggerTime());
        //Toast.makeText(this.view, "Alarm is canceled", Toast.LENGTH_SHORT).show();
    }

    public void removeSoundNotificationFrom(String id) {
        try (EventService eventService = new EventServiceImpl(view.getApplicationContext())) {
            eventService.removeSoundNotification(id);
        } catch (Exception e) {
            Log.e(TAG, "Error removing sound notification", e);
        }
    }

    public void removeSilentNotificationFrom(String id) {
        try (EventService eventService = new EventServiceImpl(view.getApplicationContext())) {
            eventService.removeSilentNotification(id);
            SilentNotificationWorker.cancelSilentNotification(view.getApplicationContext(), id);
        } catch (Exception e) {
            Log.e(TAG, "Error removing silent notification", e);
        }
    }


}
