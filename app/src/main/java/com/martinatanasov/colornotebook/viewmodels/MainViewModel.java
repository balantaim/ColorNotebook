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
import com.martinatanasov.colornotebook.views.main.OrderFilter;
import com.martinatanasov.colornotebook.views.main.PriorityFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final EventService eventService;
    private final PreferencesManager preferencesManager;
    private final MutableLiveData<List<UserEvent>> _events = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<OrderFilter> _orderFilter = new MutableLiveData<>(OrderFilter.DATE);
    private final MutableLiveData<PriorityFilter> _priorityFilter = new MutableLiveData<>(PriorityFilter.NONE);
    private final MediatorLiveData<List<UserEvent>> _filteredEvents = new MediatorLiveData<>();
    private final MutableLiveData<Integer> _importantCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _regularCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _unimportantCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _soundNotificationsCount = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> _isDataEmpty = new MutableLiveData<>(true);
    public LiveData<List<UserEvent>> events = _events;
    public LiveData<String> searchQuery = _searchQuery;
    public LiveData<OrderFilter> orderFilter = _orderFilter;
    public LiveData<PriorityFilter> priorityFilter = _priorityFilter;
    public LiveData<List<UserEvent>> filteredEvents = _filteredEvents;
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

    private void applyFilter() {
        List<UserEvent> allEvents = _events.getValue();
        String query = _searchQuery.getValue();
        OrderFilter order = _orderFilter.getValue();
        PriorityFilter priority = _priorityFilter.getValue();

        if (allEvents == null) {
            _filteredEvents.setValue(new ArrayList<>());
            return;
        }

        List<UserEvent> result = new ArrayList<>(allEvents);

        // Apply Search
        if (query != null && !query.isEmpty()) {
            String search = query.toLowerCase();
            List<UserEvent> filtered = new ArrayList<>();
            for (UserEvent event : result) {
                if (event.txtEventTitle().toLowerCase().contains(search) ||
                        event.txtNode().toLowerCase().contains(search) ||
                        event.txtEventLocation().toLowerCase().contains(search)) {
                    filtered.add(event);
                }
            }
            result = filtered;
        }

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
        Collections.sort(result, (e1, e2) -> {
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
                    case DATE -> Long.compare(e2.long_created_date(), e1.long_created_date());
                    case REVERSE_DATE ->
                            Long.compare(e1.long_created_date(), e2.long_created_date());
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
    public void setEvents(List<UserEvent> events) {
        _events.setValue(events);
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
