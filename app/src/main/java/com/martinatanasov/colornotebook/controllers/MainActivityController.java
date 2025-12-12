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

import android.os.Build;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.martinatanasov.colornotebook.dto.UserEvent;
import com.martinatanasov.colornotebook.repositories.PreferencesManager;
import com.martinatanasov.colornotebook.services.EventService;
import com.martinatanasov.colornotebook.services.EventServiceImpl;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.utils.events.NotificationCreator;
import com.martinatanasov.colornotebook.utils.events.SilentNotificationWorker;
import com.martinatanasov.colornotebook.views.main.MainActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivityController {

    private final MainActivity mainView;
    private final EventService eventService;
    private int unimportant = 0, regular = 0, important = 0, event_sound_notifications = 0;
    private final PreferencesManager preferencesManager;
    private boolean isAvailableData = false;
    private List<UserEvent> events;

    public MainActivityController(MainActivity mainView) {
        this.mainView = mainView;
        this.eventService = new EventServiceImpl(this.mainView.getApplicationContext());
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
        //Update the events
        events = eventService.getUserEventDto();
        if (events.isEmpty()) {
            mainView.printDatabaseEmpty();
        } else {
            for (UserEvent index : events) {
                if (index.int_sound_notifications() > 0) {
                    event_sound_notifications++;
                }
                switch (index.int_avatar_picker()) {
                    case 1 -> regular++;
                    case 2 -> unimportant++;
                    default -> important++;
                }
            }
        }
        //Update UI
        setInitialRecyclerView();
        //shrink FAB
        mainView.shrinkMenuButton();
        //Update drawer count/statistic
        mainView.createDrawerCounters(important, regular, unimportant, event_sound_notifications, events.size());
        //set isAvailableData boolean
        isAvailableData = events.isEmpty();
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
        important = regular = unimportant = 0;
        eventService.deleteAllEvents();
    }

    private void removeAllSoundAlarms() {
        AlarmEvent alarmEvent = new AlarmEvent(mainView);
        alarmEvent.cancelAllAlarms();
    }

    public void removeRowOnSwipe(String idString) {
        eventService.deleteEventOnOneRow(idString);
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
