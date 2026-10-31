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

package com.martinatanasov.oasis.view.custom;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.martinatanasov.oasis.R;
import com.martinatanasov.oasis.repository.PreferencesManager;
import com.martinatanasov.oasis.util.AppSettings;
import com.martinatanasov.oasis.util.ColorUtil;
import com.martinatanasov.oasis.util.ScreenManager;
import com.martinatanasov.oasis.viewmodel.CustomViewModel;

public class CustomActivity extends AppCompatActivity implements AppSettings {

    Button cancelBtn;
    TextView titleTxt, nodeTxt;
    ImageView priorityIcon;
    CustomViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Load skin resource
        updateAppSettings();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        viewModel = new ViewModelProvider(this).get(CustomViewModel.class);

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
        cancelBtn.setOnClickListener(view -> viewModel.cancelAlarm(id));
    }

    private void initScreenManager() {
        new ScreenManager(this, findViewById(R.id.root_layout_custom));
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
        return ColorUtil.getEventColorRes(color);
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

    private void initViews() {
        cancelBtn = findViewById(R.id.cancelAlarm);
        titleTxt = findViewById(R.id.txtHeader);
        nodeTxt = findViewById(R.id.txtNode);
        priorityIcon = findViewById(R.id.priorityIcon);
    }

    //Save Instance when you rotate the device or use recreate
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //Restore the instance settings
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}