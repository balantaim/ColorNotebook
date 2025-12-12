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

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.martinatanasov.colornotebook.dto.UpdateEvent;
import com.martinatanasov.colornotebook.services.EventService;
import com.martinatanasov.colornotebook.services.EventServiceImpl;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.views.update.UpdateActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateActivityController {

    private final UpdateActivity updateView;
    private final Handler handler;
    private final ExecutorService executorService;
    private final EventService eventService;

    public UpdateActivityController(UpdateActivity updateView) {
        this.updateView = updateView;
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        timerEventUpdate();
        this.eventService = new EventServiceImpl(updateView.getApplicationContext());
    }

    public void updateUserEvent(UpdateEvent updateEvent) {
        eventService.updateEvent(updateEvent);
    }

    public void deleteCurrentEvent(String eventID) {
        cancelSoundNotification(eventID);
        eventService.deleteEventOnOneRow(eventID);
    }

    private void cancelSoundNotification(String eventID) {
        AlarmEvent alarmEvent = new AlarmEvent(updateView);
        alarmEvent.cancelAlarm(eventID);
    }

    private void timerEventUpdate() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(getClass().getName(), "Error: " + e);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateView.updateOnConfigurationChanges();
                        updateView.checkIfCardIsExpanded();
                        updateView.getAndSetIntentData();
                        updateView.updateSwValues();
                    }
                });
            }
        });
    }

}
