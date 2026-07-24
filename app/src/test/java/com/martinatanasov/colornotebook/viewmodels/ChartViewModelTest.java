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

package com.martinatanasov.colornotebook.viewmodels;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ChartViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private ChartViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new ChartViewModel();
    }

    @Test
    public void testCalculatePieChartData() {
        viewModel.calculatePieChartData("1", "1", "2"); // Total 4

        assertEquals(25.0f, viewModel.importantPercent.getValue(), 0.01f);
        assertEquals(25.0f, viewModel.regularPercent.getValue(), 0.01f);
        assertEquals(50.0f, viewModel.unimportantPercent.getValue(), 0.01f);
    }

    @Test
    public void testCalculatePieChartDataZeroTotal() {
        viewModel.calculatePieChartData("0", "0", "0");

        assertEquals(0.0f, viewModel.importantPercent.getValue(), 0.01f);
        assertEquals(0.0f, viewModel.regularPercent.getValue(), 0.01f);
        assertEquals(0.0f, viewModel.unimportantPercent.getValue(), 0.01f);
    }

}
