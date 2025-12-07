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

import android.database.Cursor;
import android.os.Build;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.martinatanasov.colornotebook.repositories.MyDatabaseHelper;
import com.martinatanasov.colornotebook.dto.UserEvent;
import com.martinatanasov.colornotebook.utils.PreferencesManager;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.utils.events.NotificationCreator;
import com.martinatanasov.colornotebook.utils.events.SilentNotificationWorker;
import com.martinatanasov.colornotebook.views.main.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivityController {

    private final MainActivity mainView;
    private int unimportant = 0, regular = 0, important = 0, event_sound_notifications = 0;
    private final PreferencesManager preferencesManager;
    private boolean isAvailableData = false;
    private List<UserEvent> events;

    public MainActivityController(MainActivity mainView) {
        this.mainView = mainView;
        preferencesManager = new PreferencesManager(this.mainView, false, true);
        boolean disableTutorial = checkTutorial();
        if (!disableTutorial) {
            mainView.loadTutorial();
        } else {
            storeDataInArrays();
            mainView.startForegroundService();
            createNotification();

            WorkRequest myWorkRequest =
                    new OneTimeWorkRequest.Builder(SilentNotificationWorker.class)
                            .setInitialDelay(5, TimeUnit.SECONDS)
                            .addTag("123")
                            .build();

            WorkManager.getInstance(mainView).enqueue(myWorkRequest);
        }
    }

    public void storeDataInArrays() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(mainView.getApplicationContext());

        Cursor cursor = myDB.readAllData();
        List<UserEvent> userEvent = new ArrayList<>();
        if (cursor.getCount() == 0) {
            mainView.printDatabaseEmpty();
        } else {
            while (cursor.moveToNext()) {
                if (Integer.parseInt(cursor.getString(19)) > 0) {
                    event_sound_notifications++;
                }
                switch (Integer.parseInt(cursor.getString(5))) {
                    case 1 -> regular++;
                    case 2 -> unimportant++;
                    default -> important++;
                }
                userEvent.add(new UserEvent(
                        cursor.getString(0), //id
                        cursor.getString(1), //title
                        cursor.getString(2), //location
                        cursor.getString(3), //event_node
                        Integer.parseInt(cursor.getString(4)), //color
                        Integer.parseInt(cursor.getString(5)), //avatar
                        Integer.parseInt(cursor.getString(6)), //start_year
                        Integer.parseInt(cursor.getString(11)), //end_year
                        Integer.parseInt(cursor.getString(18)), //all_day
                        Integer.parseInt(cursor.getString(19)), //sound_notifications
                        Integer.parseInt(cursor.getString(20)), //silent_notifications
                        Byte.parseByte(cursor.getString(7)), //start_mount
                        Byte.parseByte(cursor.getString(8)), //start_day
                        Byte.parseByte(cursor.getString(9)), //start_hour
                        Byte.parseByte(cursor.getString(10)), //start_minutes
                        Byte.parseByte(cursor.getString(12)), //end_mount
                        Byte.parseByte(cursor.getString(13)), //end_day
                        Byte.parseByte(cursor.getString(14)), //end_hour
                        Byte.parseByte(cursor.getString(15)), //end_minutes
                        Long.parseLong(cursor.getString(16)), //create_date
                        Long.parseLong(cursor.getString(17)) //modified_date
                ));
            }
            //Update the events
            events = userEvent;
            //Update UI
            setInitialRecyclerView();
            //shrink FAB
            mainView.shrinkMenuButton();
        }
        //Update drawer count/statistic
        mainView.createDrawerCounters(important, regular, unimportant, event_sound_notifications, userEvent.size());
        //close cursor object
        cursor.close();

        //set isAvailableData boolean
        isAvailableData = userEvent.isEmpty();
    }

    private void setInitialRecyclerView() {
        mainView.setUpRecyclerView(events);
    }

    public void updateRecyclerView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(events, Comparator.comparing(UserEvent::txtEventTitle).reversed());
            mainView.getAdapter().notifyDataSetChanged();
        }
    }

    public void initiateChartFragment() {
        mainView.openChartFragment(important, regular, unimportant);
    }

    public void deleteBDRecords() {
        removeAllSoundAlarms();
        MyDatabaseHelper myDB = new MyDatabaseHelper(mainView.getApplicationContext());
        important = regular = unimportant = 0;
        myDB.deleteAllData();
        myDB.close();
    }

    private void removeAllSoundAlarms() {
        AlarmEvent alarmEvent = new AlarmEvent(mainView);
        alarmEvent.cancelAllAlarms();
    }

    public void removeRowOnSwipe(String idString) {
        MyDatabaseHelper myDB = new MyDatabaseHelper(this.mainView);
        myDB.deleteDataOnOneRow(idString);
        myDB.close();
    }

    private boolean checkTutorial() {
        return preferencesManager.getTutorialStatus();
    }

    private void createNotification() {
        NotificationCreator notificationCreator = new NotificationCreator();
        notificationCreator.createNotificationChannel(mainView);
//        try {
//            notificationCreator.createNotification(mainView);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        } catch (InstantiationException e) {
//            throw new RuntimeException(e);
//        }
    }

    public boolean isAvailableData() {
        return !isAvailableData;
    }

}
