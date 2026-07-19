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

package com.martinatanasov.colornotebook.views.custom;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.controllers.CustomActivityController;
import com.martinatanasov.colornotebook.repositories.PreferencesManager;
import com.martinatanasov.colornotebook.utils.AppSettings;
import com.martinatanasov.colornotebook.utils.ScreenManager;

public class CustomActivity extends AppCompatActivity implements AppSettings {
    Button cancelBtn;
    TextView titleTxt, nodeTxt;
    ImageView priorityIcon;
    CustomActivityController controller;
    private boolean isDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Load skin resource
        updateAppSettings();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        //hide Status Bar
        initScreenManager();

        initViews();

        String id = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String node = getIntent().getStringExtra("node");
        int priority = getIntent().getIntExtra("priority", 1);
        int color = getIntent().getIntExtra("color", 0);

        updateTextFields(title, node, priority, color);
        //Cancel alarm
        cancelBtn.setOnClickListener(view -> cancel(id));
    }

    private void initScreenManager() {
        new ScreenManager(findViewById(R.id.root_layout_custom),
                getWindow(),
                false);
    }

    private void updateTextFields(String title, String node, int priority, int color) {
        titleTxt.setText(title);
        nodeTxt.setText(node);
        switch (priority) {
            case 0 -> priorityIcon.setImageResource(R.drawable.ic_set_important);
            case 2 -> priorityIcon.setImageResource(R.drawable.ic_set_unimportant);
            default -> priorityIcon.setImageResource(R.drawable.ic_set_regular);
        }
        priorityIcon.setColorFilter(ContextCompat.getColor(this, getCurrentIconColor(color)), PorterDuff.Mode.SRC_IN);
    }

    private int getCurrentIconColor(int color) {
        return switch (color) {
            case 1 -> R.color.pick_sky_blue;
            case 2 -> R.color.pick_green;
            case 3 -> R.color.pick_yellow;
            case 4 -> R.color.pick_orange;
            case 5 -> R.color.error;
            case 6 -> R.color.pick_blue;
            case 7 -> R.color.pick_purple;
            case 8 -> R.color.gray_new;
            //We don't use brown yet
            default -> R.color.brown;
        };
    }

    private void cancel(String id){
        if (!isDone) {
            controller.cancelCurrentAlarm(id);
            controller.removeSoundNotificationFrom(id);
            controller.removeSilentNotificationFrom(id);
            isDone = true;
        }
    }
    private void darkModeChecker(PreferencesManager preferencesManager) {
        if (preferencesManager.getForceDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        getDelegate().applyDayNight();
    }

    @Override
    public void updateAppSettings() {
        PreferencesManager preferencesManager = new PreferencesManager(this, true, false);
        darkModeChecker(preferencesManager);

        int theme = preferencesManager.getCurrentTheme();
        switch (theme) {
            case 1 -> setTheme(R.style.Theme_BlueColorNotebook);
            case 2 -> setTheme(R.style.Theme_DarkColorNotebook);
            default -> setTheme(R.style.Theme_DefaultColorNotebook);
        }
    }
    private void initViews(){
        cancelBtn = findViewById(R.id.cancelAlarm);
        titleTxt = findViewById(R.id.txtHeader);
        nodeTxt = findViewById(R.id.txtNode);
        priorityIcon = findViewById(R.id.priorityIcon);
        controller = new CustomActivityController(this);
    }

    //Save Instance when you rotate the device or use recreate
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("isDone", isDone);

        super.onSaveInstanceState(outState);
    }
    //Restore the instance settings
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        isDone = savedInstanceState.getBoolean("isDone", false);

        super.onRestoreInstanceState(savedInstanceState);
    }
}