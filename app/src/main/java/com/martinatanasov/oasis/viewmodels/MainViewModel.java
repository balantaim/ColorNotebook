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

package com.martinatanasov.oasis.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinatanasov.oasis.BuildConfig;
import com.martinatanasov.oasis.models.UserEventDTO;
import com.martinatanasov.oasis.repositories.PreferencesManager;
import com.martinatanasov.oasis.services.EventService;
import com.martinatanasov.oasis.services.EventServiceImpl;
import com.martinatanasov.oasis.utils.events.AlarmEvent;
import com.martinatanasov.oasis.utils.events.NotificationCreator;
import com.martinatanasov.oasis.utils.events.SilentNotificationWorker;
import com.martinatanasov.oasis.views.main.OrderFilter;
import com.martinatanasov.oasis.views.main.PriorityFilter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final EventService eventService;
    private final PreferencesManager preferencesManager;
    private final MutableLiveData<List<UserEventDTO>> _events = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<OrderFilter> _orderFilter = new MutableLiveData<>(OrderFilter.DATE);
    private final MutableLiveData<PriorityFilter> _priorityFilter = new MutableLiveData<>(PriorityFilter.NONE);
    private final MediatorLiveData<List<UserEventDTO>> _filteredEvents = new MediatorLiveData<>();
    private final MutableLiveData<Integer> _importantCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _regularCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _unimportantCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _soundNotificationsCount = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> _isDataEmpty = new MutableLiveData<>(true);
    public LiveData<List<UserEventDTO>> events = _events;
    public LiveData<String> searchQuery = _searchQuery;
    public LiveData<OrderFilter> orderFilter = _orderFilter;
    public LiveData<PriorityFilter> priorityFilter = _priorityFilter;
    public LiveData<List<UserEventDTO>> filteredEvents = _filteredEvents;
    public LiveData<Integer> importantCount = _importantCount;
    public LiveData<Integer> regularCount = _regularCount;
    public LiveData<Integer> unimportantCount = _unimportantCount;
    public LiveData<Integer> soundNotificationsCount = _soundNotificationsCount;
    public LiveData<Boolean> isDataEmpty = _isDataEmpty;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.eventService = new EventServiceImpl(application);
        this.preferencesManager = new PreferencesManager(application);
        _orderFilter.setValue(preferencesManager.getOrderFilter());
        _priorityFilter.setValue(preferencesManager.getPriorityFilter());
        setupFiltering();
    }

    private void setupFiltering() {
        _filteredEvents.addSource(_events, events -> applyFilter());
        _filteredEvents.addSource(_searchQuery, query -> applyFilter());
        _filteredEvents.addSource(_orderFilter, order -> applyFilter());
        _filteredEvents.addSource(_priorityFilter, priority -> applyFilter());
        applyFilter();
    }

    @NonNull
    private static List<UserEventDTO> getEventDTOs(List<UserEventDTO> allEvents, String query) {
        List<UserEventDTO> result = new ArrayList<>(allEvents);

        // Apply Search
        if (query != null && !query.isEmpty()) {
            String search = query.toLowerCase();
            List<UserEventDTO> filtered = new ArrayList<>();
            for (UserEventDTO event : result) {
                if (event.txtEventTitle().toLowerCase().contains(search) ||
                        event.txtNode().toLowerCase().contains(search) ||
                        event.txtEventLocation().toLowerCase().contains(search)) {
                    filtered.add(event);
                }
            }
            result = filtered;
        }
        return result;
    }

    @SuppressLint("NewApi")
    private void applyFilter() {
        List<UserEventDTO> allEvents = _events.getValue();
        String query = _searchQuery.getValue();
        OrderFilter order = _orderFilter.getValue();
        PriorityFilter priority = _priorityFilter.getValue();

        if (allEvents == null) {
            _filteredEvents.setValue(new ArrayList<>());
            return;
        }

        List<UserEventDTO> result = getEventDTOs(allEvents, query);

        // Apply Priority Filter (Now as sorting primary key)
        int targetPriority = -1;
        if (priority != null && priority != PriorityFilter.NONE) {
            targetPriority = switch (priority) {
                case IMPORTANT -> 0;
                case REGULAR -> 1;
                case UNIMPORTANT -> 2;
                default -> -1;
            };
        }

        final int finalTargetPriority = targetPriority;

        // Apply Ordering & Priority Sorting
        result.sort((e1, e2) -> {
            if (finalTargetPriority != -1) {
                int p1 = e1.int_avatar_picker();
                int p2 = e2.int_avatar_picker();
                if (p1 == finalTargetPriority && p2 != finalTargetPriority) {
                    return -1;
                }
                if (p1 != finalTargetPriority && p2 == finalTargetPriority) {
                    return 1;
                }
            }

            // Apply OrderFilter
            if (order != null) {
                return switch (order) {
                    case A_Z -> e1.txtEventTitle().compareToIgnoreCase(e2.txtEventTitle());
                    case Z_A -> e2.txtEventTitle().compareToIgnoreCase(e1.txtEventTitle());
                    case DATE -> e2.instant_created_date().compareTo(e1.instant_created_date());
                    case REVERSE_DATE ->
                            e1.instant_created_date().compareTo(e2.instant_created_date());
                };
            }
            return 0;
        });

        _filteredEvents.setValue(result);
    }

    public void setSearchQuery(String query) {
        _searchQuery.setValue(query);
    }

    /**
     * Set events directly for testing purposes.
     */
    public void setEvents(List<UserEventDTO> events) {
        _events.setValue(events);
        _isDataEmpty.setValue(events.isEmpty());
        calculateCounters(events);
    }

    public void setOrderFilter(OrderFilter order) {
        _orderFilter.setValue(order);
        preferencesManager.setOrderFilter(order);
    }

    public void setPriorityFilter(PriorityFilter priority) {
        _priorityFilter.setValue(priority);
        preferencesManager.setPriorityFilter(priority);
    }

    public void init() {
        createNotificationChannel();
    }

    public boolean shouldShowTutorial() {
        return !preferencesManager.getTutorialStatus();
    }

    public void loadData() {
        List<UserEventDTO> eventList = eventService.getUserEventDto();
        _events.setValue(eventList);
        _isDataEmpty.setValue(eventList.isEmpty());
        calculateCounters(eventList);
    }

    private void calculateCounters(List<UserEventDTO> eventList) {
        int importantCount = 0, regularCount = 0, unimportantCount = 0, soundNotificationCounter = 0;
        Calendar calendarNow = Calendar.getInstance();
        for (UserEventDTO event : eventList) {
            if (event.isSoundAlarmActive(calendarNow)) {
                soundNotificationCounter++;
            }
            switch (event.int_avatar_picker()) {
                case 1 -> regularCount++;
                case 2 -> unimportantCount++;
                default -> importantCount++;
            }
        }
        _importantCount.setValue(importantCount);
        _regularCount.setValue(regularCount);
        _unimportantCount.setValue(unimportantCount);
        _soundNotificationsCount.setValue(soundNotificationCounter);
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
