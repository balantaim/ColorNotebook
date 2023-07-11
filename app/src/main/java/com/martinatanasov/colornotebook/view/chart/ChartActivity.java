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

package com.martinatanasov.colornotebook.view.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.controller.ChartActivityController;
import com.martinatanasov.colornotebook.tools.PreferencesManager;
import com.martinatanasov.colornotebook.tools.Tools;

import java.util.ArrayList;
import java.util.Objects;

public class ChartActivity extends AppCompatActivity {
    PieChart pieChart;
    private static String important = "", regular = "", unimportant = "";
    private ChartActivityController controller;
    boolean filledData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Load skin resource
        skinTheme();

        setContentView(R.layout.activity_chart);

        //Change Back arrow button
        Tools tools = new Tools();
        tools.setArrowBackIcon(Objects.requireNonNull(getSupportActionBar()));
        controller = new ChartActivityController(this);

        filledData = checkForResources();
        if(!important.equals("") && !regular.equals("") && !unimportant.equals("")){
            controller.setValues(important, regular, unimportant);
        }
        if(filledData){
            initiatePieChart();
        }else{
            String[] data = new String[3];
            data = controller.getDataValues();
            important = data[0];
            regular = data[1];
            unimportant = data[2];
            if((!important.equals("") && !regular.equals("") && !unimportant.equals("")) &&
                    (!important.equals("0") && !regular.equals("0") && !unimportant.equals("0"))
            ){
                initiatePieChart();
            }
        }
    }
    private boolean checkForResources(){
        if(getIntent().hasExtra("important") &&
                getIntent().hasExtra("regular") &&
                getIntent().hasExtra("unimportant")){
            important = getIntent().getStringExtra("important");
            regular = getIntent().getStringExtra("regular");
            unimportant = getIntent().getStringExtra("unimportant");
            //TODO

            return true;
        }else{
            //Toast.makeText(this, "Not enough data!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void initiatePieChart() {
        pieChart = findViewById(R.id.pieChart);
        ArrayList<PieEntry> data = new ArrayList<>();
        data.add(new PieEntry(Float.parseFloat(important), getResources().getString(R.string.drawer_one_priority)));
        data.add(new PieEntry(Float.parseFloat(regular), getResources().getString(R.string.drawer_two_priority)));
        data.add(new PieEntry(Float.parseFloat(unimportant), getResources().getString(R.string.drawer_three_priority)));

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
    private void skinTheme(){
        PreferencesManager preferencesManager = new PreferencesManager(this);
        switch (preferencesManager.getCurrentTheme()){
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