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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.martinatanasov.colornotebook.BuildConfig;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.controllers.OptionActivityController;
import com.martinatanasov.colornotebook.tools.PreferencesManager;

public class OptionActivity extends AppCompatActivity {

    private OptionActivityController controller;
    Switch switchDarkMode;
    TextView txtSize, txtVersion;
    Spinner spinner;
    Button btnApply;
    ImageView loadImage, shimmerView;
    ShimmerFrameLayout shimmer;
    private static int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        skinTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        //Change Back arrow button
        setupActionBar();
        //Find units by ID
        initViews();

        //Check for current version and store it in TextView
        updateVersionTxt();
        //Setup controller
        controller = new OptionActivityController(this);
        updateSwitchPosition();

        //Load image from webserver
        initiateGlideResource();

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                controller.setForceDarkValue(switchDarkMode.isChecked());
            }
        });
        spinner.setSelection(theme);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theme = position;
                controller.setTheme(position);
//                if(parent.getItemAtPosition(position).equals("Choice color")){
//                }else{
////                    String item = parent.getItemAtPosition(position).toString();
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnApply.setOnClickListener(v -> {
            startActivity(new Intent(OptionActivity.this, OptionActivity.class));
            finish();
        });

    }
    private void initiateGlideResource(){
        final String imgUrl = BuildConfig.LOADING_IMAGE + ".png";
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
    private void updateSwitchPosition(){
        switchDarkMode.setChecked(controller.getForceDarkValue());
    }
    private void updateVersionTxt(){
        final String versionApp = "v." + BuildConfig.VERSION_NAME;
        txtVersion.setText(versionApp);
    }
    private void setupActionBar(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_custom_arrow);
        //Change actionBar Background color dynamic
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#201E1E"));
        actionBar.setBackgroundDrawable(colorDrawable);
    }
    private void initViews(){
        txtVersion=findViewById(R.id.version);
        loadImage=findViewById(R.id.loadImage);
        switchDarkMode=findViewById(R.id.switchDarkMode);
        txtSize=findViewById(R.id.txtSize);
        spinner=findViewById(R.id.spinnerSkins);
        btnApply=findViewById(R.id.btnApply);
        shimmerView=findViewById(R.id.shimmerView);
        //init Shimmer container
        shimmer = findViewById(R.id.shimmerFrameLayout);
    }
    private void skinTheme(){
        PreferencesManager preferencesManager = new PreferencesManager(this);
        theme = preferencesManager.getCurrentTheme();
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

}