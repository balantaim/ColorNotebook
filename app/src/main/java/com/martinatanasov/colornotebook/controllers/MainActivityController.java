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
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import com.martinatanasov.colornotebook.model.MyDatabaseHelper;
import com.martinatanasov.colornotebook.model.UserEvent;
import com.martinatanasov.colornotebook.tools.PreferencesManager;
import com.martinatanasov.colornotebook.tools.events.AlarmEvent;
import com.martinatanasov.colornotebook.tools.events.NotificationCreator;
import com.martinatanasov.colornotebook.tools.events.SilentNotificationWorker;
import com.martinatanasov.colornotebook.views.main.MainActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivityController {

    private final MainActivity mainView;
    private int unimportant = 0, regular = 0, important = 0, event_sound_notifications = 0;
    private PreferencesManager themeManager;


    public MainActivityController(MainActivity mainView) {
        this.mainView = mainView;
        themeManager = new PreferencesManager(this.mainView, false, true);
        boolean disableTutorial = checkTutorial();
        if (!disableTutorial) {
            mainView.loadTutorial();
        } else {
            MyDatabaseHelper myDB = new MyDatabaseHelper(mainView.getApplicationContext());
            //Get the data from SQLite and update recyclerView
            storeDataInArrays();
            mainView.startForegroundService();
            createNotification();

            //close DB
            myDB.close();
            themeManager = null;

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
            mainView.emptyDB();
        } else {
            while (cursor.moveToNext()) {
                if (Integer.parseInt(cursor.getString(19)) > 0) {
                    event_sound_notifications++;
                }
                switch (Integer.parseInt(cursor.getString(5))) {
                    case 1:
                        regular++;
                        break;
                    case 2:
                        unimportant++;
                        break;
                    default:
                        important++;
                        break;
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
            //Update UI
            mainView.setUpRecyclerView(userEvent);
            //shrink FAB
            mainView.shrinkMenuButton();
        }
        //Update drawer count/statistic
        mainView.createDrawerCounters(important, regular, unimportant, event_sound_notifications, userEvent.size());
        //close cursor object
        cursor.close();
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
    private void removeAllSoundAlarms(){
        AlarmEvent alarmEvent = new AlarmEvent(mainView);
        alarmEvent.cancelAllAlarms();
    }
    public void removeRowOnSwipe(String idString){
        MyDatabaseHelper myDB = new MyDatabaseHelper(this.mainView);
        myDB.deleteDataOnOneRow(idString);
        myDB.close();
    }

    private boolean checkTutorial() {
        return themeManager.getTutorialStatus();
    }
    private void createNotification(){
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

}
