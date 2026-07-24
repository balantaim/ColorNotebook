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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinatanasov.colornotebook.services.EventService;
import com.martinatanasov.colornotebook.services.EventServiceImpl;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.utils.events.SilentNotificationWorker;

public class CustomViewModel extends AndroidViewModel {

    private final EventService eventService;
    private final MutableLiveData<Boolean> _isDone = new MutableLiveData<>(false);
    public LiveData<Boolean> isDone = _isDone;

    public CustomViewModel(@NonNull Application application) {
        super(application);
        this.eventService = new EventServiceImpl(application);
    }

    public void setDone(boolean done) {
        _isDone.setValue(done);
    }

    public void cancelAlarm(String id) {
        if (Boolean.FALSE.equals(_isDone.getValue())) {
            AlarmEvent alarm = new AlarmEvent(getApplication());
            alarm.cancelAlarm(id);

            try {
                eventService.removeSoundNotification(id);
                eventService.removeSilentNotification(id);
                SilentNotificationWorker.cancelSilentNotification(getApplication(), id);
            } catch (Exception e) {
                Log.e("CustomActivityViewModel", "Error removing notification", e);
            }
            _isDone.setValue(true);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        eventService.close();
    }

}
