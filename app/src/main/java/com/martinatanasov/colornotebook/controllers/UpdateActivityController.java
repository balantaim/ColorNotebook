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

import com.martinatanasov.colornotebook.repositories.MyDatabaseHelper;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.views.update.UpdateActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateActivityController {

    private final UpdateActivity updateView;
    private final Handler handler;
    private final ExecutorService executorService;

    public UpdateActivityController(UpdateActivity updateView) {
        this.updateView = updateView;
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        timerEventUpdate();
    }

    public void updateRecord(String id,
            String title,
            String location,
            String node,
            int color, int priority,
            int YEAR, int MONTH, int DAY, int HOUR, int MINUTES,
            int YEAR2, int MONTH2, int DAY2, int HOUR2, int MINUTES2,
            long createdTimestamp, long modifiedTimestamp, int allDay,
            int soundNotification, int silentNotification) {
        MyDatabaseHelper db = new MyDatabaseHelper(updateView.getApplicationContext());
        db.updateData(id,
                title,
                location,
                node,
                color,
                priority,
                YEAR, MONTH, DAY, HOUR, MINUTES,
                YEAR2, MONTH2, DAY2, HOUR2, MINUTES2,
                createdTimestamp,
                modifiedTimestamp,
                allDay,
                soundNotification,
                silentNotification);
        db.close();
    }

    public void deleteCurrentEvent(String eventID) {
        cancelSoundNotification(eventID);
        MyDatabaseHelper db = new MyDatabaseHelper(updateView.getApplicationContext());
        db.deleteDataOnOneRow(eventID);
        db.close();
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
