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

package com.martinatanasov.colornotebook.utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String SHARED_PREF = "sharedPref";
    private static final String THEME = "theme";
    private static final String DISABLE_TUTORIAL = "disableTutorial";
    private static final String SWITCH_DARK_MODE = "switchDarkMode";
    private static SharedPreferences sharedPreferences;
    private static int theme = -1;
    private static boolean darkThemeOn = false, disableTutorial = false;

    public PreferencesManager(Activity myActivity) {
        sharedPreferences = myActivity.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        darkThemeOn = checkForceDarkMode();
        theme = getThemeValue();
    }

    public PreferencesManager(Activity myActivity, boolean visualResources, boolean statusTutorial) {
        sharedPreferences = myActivity.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
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
