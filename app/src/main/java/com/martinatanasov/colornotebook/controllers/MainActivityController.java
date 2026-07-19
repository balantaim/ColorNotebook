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

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.martinatanasov.colornotebook.BuildConfig;
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

public class MainActivityController {

    private final MainActivity mainView;
    private final EventService eventService;
    private int unimportant = 0, regular = 0, important = 0, event_sound_notifications = 0;
    private final PreferencesManager preferencesManager;
    private boolean isDataEmpty = true;
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
            mainView.rescheduleWork();
            createNotification();
        }
    }

    public void storeDataInArrays() {
        //Reset counters
        unimportant = regular = important = event_sound_notifications = 0;
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
        //Toggle FAB
        if (events.isEmpty()) {
            mainView.extendMenuButton();
        } else {
            mainView.shrinkMenuButton();
        }
        //Update drawer count/statistic
        mainView.createDrawerCounters(important, regular, unimportant, event_sound_notifications, events.size());
        //set isDataEmpty boolean
        isDataEmpty = events.isEmpty();
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

    public void openWebsite() {
        Uri websiteUri = Uri.parse(BuildConfig.APP_WEBSITE);
        Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainView.startActivity(intent);
        mainView.moveTaskToBack(true);
    }

    public void deleteBDRecords() {
        removeAllSoundAlarms();
        SilentNotificationWorker.cancelAllSilentNotifications(mainView.getApplicationContext());
        important = regular = unimportant = 0;
        eventService.deleteAllEvents();
    }

    private void removeAllSoundAlarms() {
        AlarmEvent alarmEvent = new AlarmEvent(mainView);
        alarmEvent.cancelAllAlarms();
    }

    public void removeRowOnSwipe(String idString) {
        AlarmEvent alarmEvent = new AlarmEvent(mainView);
        alarmEvent.cancelAlarm(idString);
        SilentNotificationWorker.cancelSilentNotification(mainView.getApplicationContext(), idString);
        eventService.deleteEventOnOneRow(idString);
    }

    private boolean checkTutorial() {
        return preferencesManager.getTutorialStatus();
    }

    private void createNotification() {
        NotificationCreator notificationCreator = new NotificationCreator();
        notificationCreator.createNotificationChannel(mainView);
    }

    public boolean isAvailableData() {
        return !isDataEmpty;
    }

    public void close() {
        if (eventService != null) {
            eventService.close();
        }
    }

}
