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

import com.martinatanasov.colornotebook.repositories.PreferencesManager;

public class OptionViewModel extends AndroidViewModel {

    private final PreferencesManager preferencesManager;
    private final MutableLiveData<Integer> _currentTheme = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _forceDarkMode = new MutableLiveData<>();
    public LiveData<Integer> currentTheme = _currentTheme;
    public LiveData<Boolean> forceDarkMode = _forceDarkMode;

    public OptionViewModel(@NonNull Application application) {
        super(application);
        this.preferencesManager = new PreferencesManager(application);
        _currentTheme.setValue(preferencesManager.getCurrentTheme());
        _forceDarkMode.setValue(preferencesManager.getForceDarkMode());
    }

    public void setTheme(int themeValue) {
        preferencesManager.setThemeOnDisc(themeValue);
        _currentTheme.setValue(themeValue);
    }

    public void setForceDarkMode(boolean forceDarkValue) {
        preferencesManager.setForceDarkOnDisc(forceDarkValue);
        _forceDarkMode.setValue(forceDarkValue);
    }

}
