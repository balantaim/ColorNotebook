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

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinatanasov.colornotebook.dto.AddEvent;
import com.martinatanasov.colornotebook.services.EventService;
import com.martinatanasov.colornotebook.services.EventServiceImpl;

import java.util.Calendar;

public class AddViewModel extends AndroidViewModel {

    private final EventService eventService;
    private final MutableLiveData<Long> _eventAddedEvent = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>("");
    public MutableLiveData<String> location = new MutableLiveData<>("");
    public MutableLiveData<String> input = new MutableLiveData<>("");
    public MutableLiveData<Integer> colorPicker = new MutableLiveData<>(0);
    public MutableLiveData<Integer> priorityPicker = new MutableLiveData<>(1);
    public MutableLiveData<Integer> startYear = new MutableLiveData<>(0);
    public MutableLiveData<Integer> startMonth = new MutableLiveData<>(0);
    public MutableLiveData<Integer> startDay = new MutableLiveData<>(0);
    public MutableLiveData<Integer> startHour = new MutableLiveData<>(0);
    public MutableLiveData<Integer> startMinutes = new MutableLiveData<>(0);
    public MutableLiveData<Integer> endYear = new MutableLiveData<>(0);
    public MutableLiveData<Integer> endMonth = new MutableLiveData<>(0);
    public MutableLiveData<Integer> endDay = new MutableLiveData<>(0);
    public MutableLiveData<Integer> endHour = new MutableLiveData<>(0);
    public MutableLiveData<Integer> endMinutes = new MutableLiveData<>(0);
    public MutableLiveData<Boolean> isAllDay = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isSoundNotification = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isSilentNotification = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isExpanded = new MutableLiveData<>(false);
    public LiveData<Long> eventAddedEvent = _eventAddedEvent;

    public AddViewModel(@NonNull Application application) {
        super(application);
        this.eventService = new EventServiceImpl(application);
        initDates();
    }

    private void initDates() {
        Calendar calendar = Calendar.getInstance();
        startYear.setValue(calendar.get(Calendar.YEAR));
        startMonth.setValue(calendar.get(Calendar.MONTH));
        startDay.setValue(calendar.get(Calendar.DATE));
        startHour.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        startMinutes.setValue(calendar.get(Calendar.MINUTE));

        endYear.setValue(calendar.get(Calendar.YEAR));
        endMonth.setValue(calendar.get(Calendar.MONTH));
        endDay.setValue(calendar.get(Calendar.DATE));
        endHour.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        endMinutes.setValue(calendar.get(Calendar.MINUTE));
    }

    public void addEvent() {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        AddEvent addEvent = new AddEvent(
                title.getValue(),
                location.getValue(),
                input.getValue(),
                colorPicker.getValue(),
                priorityPicker.getValue(),
                startYear.getValue(), startMonth.getValue(), startDay.getValue(), startHour.getValue(), startMinutes.getValue(),
                endYear.getValue(), endMonth.getValue(), endDay.getValue(), endHour.getValue(), endMinutes.getValue(),
                timestamp,
                timestamp,
                isAllDay.getValue() ? 1 : 0,
                isSoundNotification.getValue() ? 1 : 0,
                isSilentNotification.getValue() ? 1 : 0
        );
        long newId = eventService.addEvent(addEvent);
        _eventAddedEvent.setValue(newId);
    }

    public void toggleExpanded() {
        isExpanded.setValue(!Boolean.TRUE.equals(isExpanded.getValue()));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        eventService.close();
    }

}
