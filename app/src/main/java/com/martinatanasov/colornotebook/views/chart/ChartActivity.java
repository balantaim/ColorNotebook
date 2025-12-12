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

package com.martinatanasov.colornotebook.views.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.controllers.ChartActivityController;
import com.martinatanasov.colornotebook.repositories.PreferencesManager;
import com.martinatanasov.colornotebook.utils.AppSettings;
import com.martinatanasov.colornotebook.utils.ScreenManager;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity implements AppSettings {
    PieChart pieChart;
    private ChartActivityController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Load skin resource
        updateAppSettings();

        setContentView(R.layout.activity_chart);

        initScreenManager();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_chart);
        setSupportActionBar(toolbar);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);// show back arrow
//            Drawable arrow = getResources().getDrawable(R.drawable.ic_custom_arrow);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                arrow.setTint(getResources().getColor(R.color.white, getTheme())); // set color
//            }
//            getSupportActionBar().setHomeAsUpIndicator(arrow);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_custom_arrow);
        }

        //Change Back arrow button
//        changeArrowBackBtn();

        controller = new ChartActivityController(this);

        checkForResources();

        controller.calcPieChartData();
        initiatePieChart();
    }

    private void initScreenManager() {
        new ScreenManager(findViewById(R.id.root_layout_chart),
                getWindow(),
                false);
    }
    private void checkForResources(){
        String important = "", regular = "", unimportant = "";
        if(getIntent().hasExtra("important") &&
                getIntent().hasExtra("regular") &&
                getIntent().hasExtra("unimportant")){
            important = getIntent().getStringExtra("important");
            regular = getIntent().getStringExtra("regular");
            unimportant = getIntent().getStringExtra("unimportant");

            controller.setValues(important, regular, unimportant);
        }else{
            Toast.makeText(this, "Not enough data!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initiatePieChart() {
        pieChart = findViewById(R.id.pieChart);
        ArrayList<PieEntry> data = new ArrayList<>();
        data.add(new PieEntry(controller.getImportantPercent(), getResources().getString(R.string.drawer_one_priority) + " %"));
        data.add(new PieEntry(controller.getRegularPercent(), getResources().getString(R.string.drawer_two_priority) + " %"));
        data.add(new PieEntry(controller.getUnimportantDouble(), getResources().getString(R.string.drawer_three_priority) + " %"));

        PieDataSet pieDataSet = new PieDataSet(data, getResources().getString(R.string.events));
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(18f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(getResources().getString(R.string.events));
        pieChart.animate();
    }

    //    private void changeArrowBackBtn(){
//        ActionBarIconSetter actionBarIconSetter = new ActionBarIconSetter();
//        actionBarIconSetter.setArrowBackIcon(Objects.requireNonNull(getSupportActionBar()));
//        controller = new ChartActivityController(this);
//    }
    @Override
    public void updateAppSettings() {
        PreferencesManager preferencesManager = new PreferencesManager(this);
        switch (preferencesManager.getCurrentTheme()) {
            case 1 -> setTheme(R.style.Theme_BlueColorNotebook);
            case 2 -> setTheme(R.style.Theme_DarkColorNotebook);
            default -> setTheme(R.style.Theme_DefaultColorNotebook);
        }
    }
}