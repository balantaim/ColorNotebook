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

import android.app.Activity;

import com.martinatanasov.colornotebook.model.MyDatabaseHelper;
import com.martinatanasov.colornotebook.tools.events.AlarmEvent;
import com.martinatanasov.colornotebook.view.custom.CustomActivity;

public class CustomActivityController {
    final Activity view;

    public CustomActivityController(CustomActivity view){
        this.view = view;
    }
    public void cancelCurrentAlarm(String id){
        AlarmEvent alarm = new AlarmEvent(this.view);
        alarm.cancelAlarm(id);
        //Toast.makeText(this.view, "Alarm is canceled", Toast.LENGTH_SHORT).show();
    }

    public void removeSoundNotificationFrom(String id){
        MyDatabaseHelper myDB = new MyDatabaseHelper(view.getApplicationContext());
        myDB.removeSoundNotification(id);
        myDB.close();
    }


}
