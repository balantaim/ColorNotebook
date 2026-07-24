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
import static org.junit.Assert.assertNotNull;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class OptionViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private OptionViewModel viewModel;

    @Before
    public void setUp() {
        Application application = ApplicationProvider.getApplicationContext();
        viewModel = new OptionViewModel(application);
    }

    @Test
    public void testInitialState() {
        assertNotNull(viewModel.currentTheme.getValue());
        assertNotNull(viewModel.forceDarkMode.getValue());
    }

    @Test
    public void testSetTheme() {
        viewModel.setTheme(1);
        assertEquals(1, (int) viewModel.currentTheme.getValue());
    }

    @Test
    public void testSetForceDarkMode() {
        viewModel.setForceDarkMode(true);
        assertEquals(true, viewModel.forceDarkMode.getValue());
    }

}
