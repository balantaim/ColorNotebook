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

package com.martinatanasov.colornotebook.controllers;

import com.martinatanasov.colornotebook.views.chart.ChartActivity;

public class ChartActivityController {

    private final ChartActivity chartActivity;
    private static String important = "", regular = "", unimportant = "";
    private static float importantDouble = 0.0F, regularDouble = 0.0F, unimportantDouble = 0.0F;

    public ChartActivityController(ChartActivity chartActivity) {
        this.chartActivity = chartActivity;
    }

    public void setValues(String imp, String reg, String uni) {
        important = imp;
        regular = reg;
        unimportant = uni;
    }

    public void calcPieChartData() {
        int importantCount = Integer.parseInt(important);
        int regularCount = Integer.parseInt(regular);
        int unimportantCount = Integer.parseInt(unimportant);
        float percentPerPart = (float) 100 / (importantCount + regularCount + unimportantCount);
        importantDouble = (float) percentPerPart * importantCount;
        regularDouble = (float) percentPerPart * regularCount;
        unimportantDouble = (float) percentPerPart * unimportantCount;
    }

    public float getImportantPercent() {
        return importantDouble;
    }

    public float getRegularPercent() {
        return regularDouble;
    }

    public float getUnimportantDouble() {
        return unimportantDouble;
    }
}
