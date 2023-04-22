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
import com.martinatanasov.colornotebook.tools.Tools;

import java.util.ArrayList;
import java.util.Objects;

public class ChartActivity extends AppCompatActivity {
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_chart);

        //Change Back arrow button
        Tools tools = new Tools();
        tools.setArrowBackIcon(Objects.requireNonNull(getSupportActionBar()));

        initiatePieChart();
    }

    private void initiatePieChart() {
        pieChart = findViewById(R.id.pieChart);
        ArrayList<PieEntry> data = new ArrayList<>();
        data.add(new PieEntry(10, getResources().getString(R.string.drawer_one_priority)));
        data.add(new PieEntry(20, getResources().getString(R.string.drawer_two_priority)));
        data.add(new PieEntry(30, getResources().getString(R.string.drawer_three_priority)));

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
}