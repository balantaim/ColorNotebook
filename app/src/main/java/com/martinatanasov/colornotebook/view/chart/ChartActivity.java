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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.controller.ChartActivityController;
import com.martinatanasov.colornotebook.tools.ActionBarIconSetter;
import com.martinatanasov.colornotebook.tools.PreferencesManager;
import java.util.ArrayList;
import java.util.Objects;

public class ChartActivity extends AppCompatActivity {
    PieChart pieChart;
    private ChartActivityController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Load skin resource
        skinTheme();

        setContentView(R.layout.activity_chart);

        //Change Back arrow button
        changeArrowBackBtn();

        checkForResources();

        controller.calcPieChartData();
        initiatePieChart();
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
    private void changeArrowBackBtn(){
        ActionBarIconSetter actionBarIconSetter = new ActionBarIconSetter();
        actionBarIconSetter.setArrowBackIcon(Objects.requireNonNull(getSupportActionBar()));
        controller = new ChartActivityController(this);
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