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

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.controllers.CustomActivityController;
import com.martinatanasov.colornotebook.tools.PreferencesManager;

public class CustomActivity extends AppCompatActivity {
    Button cancelBtn;
    TextView titleTxt, nodeTxt;
    CustomActivityController controller;
    private boolean isDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Load skin resource
        skinTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();

        String id = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String node = getIntent().getStringExtra("node");

        updateTextFields(title, node);
        //Cancel alarm
        cancelBtn.setOnClickListener(view -> cancel(id));
    }

    private void updateTextFields(String title, String node) {
        titleTxt.setText(title);
        nodeTxt.setText(node);
    }

    private void cancel(String id){
        if (!isDone) {
            controller.cancelCurrentAlarm(id);
            controller.removeSoundNotificationFrom(id);
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
    private void skinTheme() {
        PreferencesManager preferencesManager = new PreferencesManager(this, true, false);
        darkModeChecker(preferencesManager);

        int theme = preferencesManager.getCurrentTheme();
        switch (theme) {
            case 1:
                setTheme(R.style.Theme_BlueColorNotebook);
                break;
            case 2:
                setTheme(R.style.Theme_DarkColorNotebook);
                break;
            default:
                setTheme(R.style.Theme_DefaultColorNotebook);
                break;
        }
    }
    private void initViews(){
        cancelBtn = findViewById(R.id.cancelAlarm);
        titleTxt = findViewById(R.id.txtHeader);
        nodeTxt = findViewById(R.id.txtNode);
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