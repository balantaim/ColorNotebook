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

package com.martinatanasov.colornotebook.controller;


import android.database.Cursor;
import android.util.Log;
import com.martinatanasov.colornotebook.model.MyDatabaseHelper;
import com.martinatanasov.colornotebook.model.UserEvent;
import com.martinatanasov.colornotebook.tools.PreferencesManager;
import com.martinatanasov.colornotebook.view.main.MainActivity;
import java.util.ArrayList;

public class MainActivityController {

    private MainActivity mainView;
    private MyDatabaseHelper myDB;
    private ArrayList<UserEvent> userEvent;
    private int unimportant = 0, regular = 0, important = 0, event_sound_notifications = 0;
    private PreferencesManager themeManager;


    public MainActivityController(MainActivity mainView) {
        this.mainView = mainView;
        themeManager = new PreferencesManager(this.mainView, false, true);
        boolean disableTutorial = checkTutorial();
        if (!disableTutorial) {
            mainView.loadTutorial();
        } else {
            myDB = new MyDatabaseHelper(mainView.getApplicationContext());
            userEvent = new ArrayList<>();
            //Get the data from SQLite and update recyclerView
            storeDataInArrays();
            mainView.startForegroundService();

            themeManager = null;
        }
    }

    void storeDataInArrays() {
        Cursor cursor = myDB.readAllData();
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
            Log.d("debugME", "storeDataInArrays: " + userEvent.get(0).getTxtEventTitle());
        }
        //Update drawer count/statistic
        mainView.createDrawerCounters(important, regular, unimportant, event_sound_notifications, userEvent.size());
    }

    public void initiateChartFragment() {
        mainView.openChartFragment(important, regular, unimportant);
    }

    public void deleteBDRecords() {
        important = regular = unimportant = 0;
        myDB.deleteAllData();
    }

    private boolean checkTutorial() {
        return themeManager.getTutorialStatus();
    }

}
