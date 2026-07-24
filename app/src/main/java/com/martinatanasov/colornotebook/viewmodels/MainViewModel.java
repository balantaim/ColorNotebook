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

package com.martinatanasov.colornotebook.viewmodels;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinatanasov.colornotebook.BuildConfig;
import com.martinatanasov.colornotebook.dto.UserEvent;
import com.martinatanasov.colornotebook.repositories.PreferencesManager;
import com.martinatanasov.colornotebook.services.EventService;
import com.martinatanasov.colornotebook.services.EventServiceImpl;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.utils.events.NotificationCreator;
import com.martinatanasov.colornotebook.utils.events.SilentNotificationWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final EventService eventService;
    private final PreferencesManager preferencesManager;
    private final MutableLiveData<List<UserEvent>> _events = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _searchQuery = new MutableLiveData<>("");
    private final MediatorLiveData<List<UserEvent>> _filteredEvents = new MediatorLiveData<>();
    private final MutableLiveData<Integer> _importantCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _regularCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _unimportantCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _soundNotificationsCount = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> _isDataEmpty = new MutableLiveData<>(true);
    public LiveData<List<UserEvent>> events = _events;
    public LiveData<String> searchQuery = _searchQuery;
    public LiveData<List<UserEvent>> filteredEvents = _filteredEvents;
    public LiveData<Integer> importantCount = _importantCount;
    public LiveData<Integer> regularCount = _regularCount;
    public LiveData<Integer> unimportantCount = _unimportantCount;
    public LiveData<Integer> soundNotificationsCount = _soundNotificationsCount;
    public LiveData<Boolean> isDataEmpty = _isDataEmpty;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.eventService = new EventServiceImpl(application);
        this.preferencesManager = new PreferencesManager(application, false, true);
        setupFiltering();
    }

    private void setupFiltering() {
        _filteredEvents.addSource(_events, events -> applyFilter());
        _filteredEvents.addSource(_searchQuery, query -> applyFilter());
        applyFilter();
    }

    private void applyFilter() {
        List<UserEvent> allEvents = _events.getValue();
        String query = _searchQuery.getValue();

        if (allEvents == null) {
            _filteredEvents.setValue(new ArrayList<>());
            return;
        }

        if (query == null || query.isEmpty()) {
            _filteredEvents.setValue(allEvents);
            return;
        }

        String search = query.toLowerCase();
        List<UserEvent> filtered = new ArrayList<>();
        for (UserEvent event : allEvents) {
            if (event.txtEventTitle().toLowerCase().contains(search) ||
                    event.txtNode().toLowerCase().contains(search) ||
                    event.txtEventLocation().toLowerCase().contains(search)) {
                filtered.add(event);
            }
        }
        _filteredEvents.setValue(filtered);
    }

    public void setSearchQuery(String query) {
        _searchQuery.setValue(query);
    }

    public void init() {
        createNotificationChannel();
    }

    public boolean shouldShowTutorial() {
        return !preferencesManager.getTutorialStatus();
    }

    public void loadData() {
        List<UserEvent> eventList = eventService.getUserEventDto();
        _events.setValue(eventList);
        _isDataEmpty.setValue(eventList.isEmpty());
        calculateCounters(eventList);
    }

    private void calculateCounters(List<UserEvent> eventList) {
        int imp = 0, reg = 0, uni = 0, sound = 0;
        for (UserEvent event : eventList) {
            if (event.int_sound_notifications() > 0) {
                sound++;
            }
            switch (event.int_avatar_picker()) {
                case 1 -> reg++;
                case 2 -> uni++;
                default -> imp++;
            }
        }
        _importantCount.setValue(imp);
        _regularCount.setValue(reg);
        _unimportantCount.setValue(uni);
        _soundNotificationsCount.setValue(sound);
    }

    public void sortEvents() {
        List<UserEvent> currentEvents = _events.getValue();
        if (currentEvents != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<UserEvent> sorted = new ArrayList<>(currentEvents);
            Collections.sort(sorted, Comparator.comparing(UserEvent::txtEventTitle).reversed());
            _events.setValue(sorted);
        }
    }

    public void deleteBatch() {
        AlarmEvent alarmEvent = new AlarmEvent(getApplication());
        alarmEvent.cancelAllAlarms();
        SilentNotificationWorker.cancelAllSilentNotifications(getApplication());
        eventService.deleteAllEvents();
        loadData();
    }

    public void removeEvent(String idString) {
        AlarmEvent alarmEvent = new AlarmEvent(getApplication());
        alarmEvent.cancelAlarm(idString);
        SilentNotificationWorker.cancelSilentNotification(getApplication(), idString);
        eventService.deleteEventOnOneRow(idString);
        loadData();
    }

    private void createNotificationChannel() {
        NotificationCreator notificationCreator = new NotificationCreator();
        notificationCreator.createNotificationChannel(getApplication());
    }

    public Intent getWebsiteIntent() {
        Uri websiteUri = Uri.parse(BuildConfig.APP_WEBSITE);
        Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        eventService.close();
    }

}
