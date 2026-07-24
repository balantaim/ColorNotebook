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

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class AddViewModelTest {

    private AddViewModel viewModel;

    @Before
    public void setUp() {
        Application application = ApplicationProvider.getApplicationContext();
        viewModel = new AddViewModel(application);
    }

    @Test
    public void testInitialState() {
        assertEquals("", viewModel.title.getValue());
        assertEquals(0, (int) viewModel.colorPicker.getValue());
        assertEquals(1, (int) viewModel.priorityPicker.getValue());
        assertNotNull(viewModel.startYear.getValue());
    }

    @Test
    public void testToggleExpanded() {
        assertEquals(false, viewModel.isExpanded.getValue());
        viewModel.toggleExpanded();
        assertEquals(true, viewModel.isExpanded.getValue());
        viewModel.toggleExpanded();
        assertEquals(false, viewModel.isExpanded.getValue());
    }

}
