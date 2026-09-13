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

package com.martinatanasov.colornotebook.views.option;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.martinatanasov.colornotebook.BuildConfig;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.repositories.PreferencesManager;
import com.martinatanasov.colornotebook.utils.AppSettings;
import com.martinatanasov.colornotebook.utils.ScreenManager;
import com.martinatanasov.colornotebook.viewmodels.OptionViewModel;

import java.util.Objects;

public class OptionActivity extends AppCompatActivity implements AppSettings {

    private OptionViewModel viewModel;
    Switch switchDarkMode;
    TextView txtVersion;
    Spinner spinnerThemeColor, spinnerLanguage;
    ImageView loadImage, shimmerView;
    ShimmerFrameLayout shimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateAppSettings();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        viewModel = new ViewModelProvider(this).get(OptionViewModel.class);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_option);
        setSupportActionBar(toolbar);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_custom_arrow);
        }

        //Hide Status Bar
        initScreenManager();
        //Find units by ID
        initViews();

        //Check for current version and store it in TextView
        updateVersionTxt();
        initObservers();
        //Load image from webserver
        initiateGlideResource();

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                viewModel.setForceDarkMode(isChecked);
            }
        });

        spinnerThemeColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setTheme(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppLanguage language = switch (position) {
                    case 1 -> AppLanguage.ENGLISH;
                    case 2 -> AppLanguage.BULGARIAN;
                    case 3 -> AppLanguage.RUSSIAN;
                    default -> AppLanguage.SYSTEM_DEFAULT;
                };

                // Only apply if it's different from current application locales to avoid loops
                LocaleListCompat currentAppLocales = AppCompatDelegate.getApplicationLocales();
                AppLanguage currentAppLanguage = currentAppLocales.isEmpty() ?
                        AppLanguage.SYSTEM_DEFAULT :
                        AppLanguage.fromTag(Objects.requireNonNull(currentAppLocales.get(0)).getLanguage());

                if (language != currentAppLanguage) {
                    viewModel.setLanguage(language);
                    applyLanguage(language.getTag());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void applyLanguage(String languageTag) {
        LocaleListCompat appLocales = languageTag.isEmpty() ?
                LocaleListCompat.getEmptyLocaleList() :
                LocaleListCompat.forLanguageTags(languageTag);
        AppCompatDelegate.setApplicationLocales(appLocales);
    }

    private void initObservers() {
        viewModel.forceDarkMode.observe(this, isChecked -> {
            if (switchDarkMode.isChecked() != isChecked) {
                switchDarkMode.setChecked(isChecked);
            }
        });
        viewModel.currentTheme.observe(this, t -> {
            if (spinnerThemeColor.getSelectedItemPosition() != t) {
                spinnerThemeColor.setSelection(t);
            }
        });
        viewModel.currentLanguage.observe(this, lang -> {
            int position = switch (lang) {
                case ENGLISH -> 1;
                case BULGARIAN -> 2;
                case RUSSIAN -> 3;
                default -> 0;
            };
            if (spinnerLanguage.getSelectedItemPosition() != position) {
                spinnerLanguage.setSelection(position);
            }
        });
    }

    private void initScreenManager() {
        new ScreenManager(findViewById(R.id.root_layout_option),
                getWindow(),
                false);
    }

    private void initiateGlideResource() {
        final String imgUrl = BuildConfig.LOADING_IMAGE;
        Glide.with(OptionActivity.this)
                .load(imgUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        shimmer.stopShimmer();
                        shimmerView.setVisibility(View.GONE);
                        loadImage.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        shimmer.stopShimmer();
                        shimmerView.setVisibility(View.GONE);
                        loadImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .timeout(8000)
                .fitCenter()
                //reload image every time
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.ic_logo)
                .into(loadImage);
    }

    private void updateVersionTxt() {
        final String versionApp = "v." + BuildConfig.VERSION_NAME;
        txtVersion.setText(versionApp);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_custom_arrow);
        //Change actionBar Background color dynamic
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#201E1E"));
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    private void initViews() {
        txtVersion = findViewById(R.id.version);
        loadImage = findViewById(R.id.loadImage);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        spinnerThemeColor = findViewById(R.id.spinnerSkins);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        shimmerView = findViewById(R.id.shimmerView);
        //init Shimmer container
        shimmer = findViewById(R.id.shimmerFrameLayout);
    }

    @Override
    public void updateAppSettings() {
        PreferencesManager preferencesManager = new PreferencesManager(this);
        int theme = preferencesManager.getCurrentTheme();
        switch (theme) {
            case 1 -> setTheme(R.style.Theme_BlueColorNotebook);
            case 2 -> setTheme(R.style.Theme_DarkColorNotebook);
            default -> setTheme(R.style.Theme_DefaultColorNotebook);
        }
    }

}
