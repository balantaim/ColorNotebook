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

import com.martinatanasov.colornotebook.dto.UpdateEvent;
import com.martinatanasov.colornotebook.services.EventService;
import com.martinatanasov.colornotebook.services.EventServiceImpl;

import java.util.Calendar;

public class UpdateViewModel extends AndroidViewModel {

    private final EventService eventService;
    private final MutableLiveData<Boolean> _eventUpdatedEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _eventDeletedEvent = new MutableLiveData<>();
    public MutableLiveData<String> id = new MutableLiveData<>("");
    public MutableLiveData<String> title = new MutableLiveData<>("");
    public MutableLiveData<String> location = new MutableLiveData<>("");
    public MutableLiveData<String> input = new MutableLiveData<>("");
    public MutableLiveData<Integer> colorPicker = new MutableLiveData<>(0);
    public MutableLiveData<Integer> priorityPicker = new MutableLiveData<>(0);
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
    public MutableLiveData<Long> createdDate = new MutableLiveData<>(0L);
    public MutableLiveData<Long> modifiedDate = new MutableLiveData<>(0L);
    public MutableLiveData<Boolean> isAllDay = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isSoundNotification = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isSilentNotification = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isExpanded = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isDeleteDialogShowing = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isStartDatePickerShowing = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isEndDatePickerShowing = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isStartTimePickerShowing = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isEndTimePickerShowing = new MutableLiveData<>(false);
    public LiveData<Boolean> eventUpdatedEvent = _eventUpdatedEvent;
    public LiveData<Boolean> eventDeletedEvent = _eventDeletedEvent;

    public UpdateViewModel(@NonNull Application application) {
        super(application);
        this.eventService = new EventServiceImpl(application);
    }

    public void updateEvent() {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        UpdateEvent updateEvent = new UpdateEvent(
                id.getValue(),
                title.getValue(),
                location.getValue(),
                input.getValue(),
                colorPicker.getValue(),
                priorityPicker.getValue(),
                startYear.getValue(), startMonth.getValue(), startDay.getValue(), startHour.getValue(), startMinutes.getValue(),
                endYear.getValue(), endMonth.getValue(), endDay.getValue(), endHour.getValue(), endMinutes.getValue(),
                createdDate.getValue(),
                timestamp,
                isAllDay.getValue() ? 1 : 0,
                isSoundNotification.getValue() ? 1 : 0,
                isSilentNotification.getValue() ? 1 : 0
        );
        eventService.updateEvent(updateEvent);
        _eventUpdatedEvent.setValue(true);
    }

    public void deleteEvent() {
        eventService.deleteEventOnOneRow(id.getValue());
        _eventDeletedEvent.setValue(true);
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
