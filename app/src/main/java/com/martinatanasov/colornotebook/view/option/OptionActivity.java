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

package com.martinatanasov.colornotebook.view.option;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class OptionActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private TextView txtSize, txtVersion;
    private Spinner spinner;
    private Button btnApply;
    private ImageView loadImage, shimmerView;

    public static final String SHARED_PREF = "sharedPref";
    public static final String THEME = "theme";
    public static final String TXT_SIZE = "txtSize";
    public static final String SWITCH_DARK_MODE = "switchDarkMode";
    private static int theme;
    private static final String imgUrl = BuildConfig.LOADING_IMAGE + ".png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loadOptions();
        skinTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        //Change Back arrow button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_custom_arrow);

        //Change actionBar Background color dynamic
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#201E1E"));
        actionBar.setBackgroundDrawable(colorDrawable);

        txtVersion=findViewById(R.id.version);
        loadImage=findViewById(R.id.loadImage);
        switchDarkMode=findViewById(R.id.switchDarkMode);
        txtSize=findViewById(R.id.txtSize);
        spinner=findViewById(R.id.spinnerSkins);
        btnApply=findViewById(R.id.btnApply);
        shimmerView=findViewById(R.id.shimmerView);

        //Check for current version and store it in TextView
        txtVersion.setText("v." + BuildConfig.VERSION_NAME);

        //init Shimmer container
        ShimmerFrameLayout shimmer = (ShimmerFrameLayout) findViewById(R.id.shimmerFrameLayout);

        //Load image from webserver
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

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        switchDarkMode.setChecked(sharedPreferences.getBoolean(SWITCH_DARK_MODE, false));

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SWITCH_DARK_MODE, switchDarkMode.isChecked());
                editor.apply();
            }
        });
        spinner.setSelection(theme);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Choice color")){

                }else{
//                    String item = parent.getItemAtPosition(position).toString();
                    theme = position;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(THEME, theme);
                    editor.apply();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionActivity.this, OptionActivity.class));
                finish();
            }
        });

    }

    private void loadOptions(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //color = sharedPreferences.getString(THEME, "");
        theme = sharedPreferences.getInt(THEME, 0);
        sharedPreferences.getBoolean(SWITCH_DARK_MODE, false);

        editor.apply();
    }

    private void skinTheme(){
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
//
//        if(sharedPreferences.getBoolean(SWITCH_DARK_MODE, false)){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            getDelegate().applyDayNight();
//        }else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//            getDelegate().applyDayNight();
//        }

        switch (theme){
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