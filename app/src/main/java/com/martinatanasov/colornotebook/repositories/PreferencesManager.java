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

package com.martinatanasov.colornotebook.repositories;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.martinatanasov.colornotebook.views.main.OrderFilter;
import com.martinatanasov.colornotebook.views.main.PriorityFilter;

public class PreferencesManager {

    private static final String SHARED_PREF = "sharedPref";
    private static final String THEME = "theme";
    private static final String DISABLE_TUTORIAL = "disableTutorial";
    private static final String SWITCH_DARK_MODE = "switchDarkMode";
    private static final String ORDER_FILTER = "orderFilter";
    private static final String PRIORITY_FILTER = "priorityFilter";
    private static SharedPreferences sharedPreferences;
    private static int theme = -1;
    private static boolean darkThemeOn = false, disableTutorial = false;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        darkThemeOn = checkForceDarkMode();
        theme = getThemeValue();
        disableTutorial = checkTutorial();
    }

    public PreferencesManager(Context context, boolean visualResources, boolean statusTutorial) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        if (visualResources) {
            darkThemeOn = checkForceDarkMode();
            theme = getThemeValue();
        }
        if (statusTutorial) {
            disableTutorial = checkTutorial();
        }
    }

    public int getCurrentTheme() {
        return theme;
    }

    public boolean getTutorialStatus() {
        return disableTutorial;
    }

    public boolean getForceDarkMode() {
        return darkThemeOn;
    }

    public void setThemeOnDisc(int themeValue) {
        theme = themeValue;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(THEME, themeValue);
        editor.apply();
    }

    public void setTutorialOnDisc(boolean tutorialValue) {
        disableTutorial = tutorialValue;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DISABLE_TUTORIAL, tutorialValue);
        editor.apply();
    }

    public void setForceDarkOnDisc(boolean forceDarkValue) {
        darkThemeOn = forceDarkValue;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCH_DARK_MODE, forceDarkValue);
        editor.apply();
    }

    public OrderFilter getOrderFilter() {
        String name = sharedPreferences.getString(ORDER_FILTER, OrderFilter.DATE.name());
        try {
            return OrderFilter.valueOf(name);
        } catch (IllegalArgumentException e) {
            return OrderFilter.DATE;
        }
    }

    public void setOrderFilter(OrderFilter orderFilter) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ORDER_FILTER, orderFilter.name());
        editor.apply();
    }

    public PriorityFilter getPriorityFilter() {
        String name = sharedPreferences.getString(PRIORITY_FILTER, PriorityFilter.NONE.name());
        try {
            return PriorityFilter.valueOf(name);
        } catch (IllegalArgumentException e) {
            return PriorityFilter.NONE;
        }
    }

    public void setPriorityFilter(PriorityFilter priorityFilter) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PRIORITY_FILTER, priorityFilter.name());
        editor.apply();
    }

    private int getThemeValue() {
        return sharedPreferences.getInt(THEME, 0);
    }

    private boolean checkTutorial() {
        return sharedPreferences.getBoolean(DISABLE_TUTORIAL, false);
    }

    private boolean checkForceDarkMode() {
        return sharedPreferences.getBoolean(SWITCH_DARK_MODE, false);
    }

}
