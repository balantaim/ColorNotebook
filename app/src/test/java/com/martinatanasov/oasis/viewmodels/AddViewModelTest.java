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

package com.martinatanasov.oasis.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AddViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private AddViewModel viewModel;

    @Before
    public void setUp() {
        Application application = ApplicationProvider.getApplicationContext();
        viewModel = new AddViewModel(application);
    }

    @After
    public void tearDown() {
        if (viewModel != null) {
            viewModel.onCleared();
        }
    }

    @Test
    public void testInitialState() {
        assertEquals("", viewModel.title.getValue());
        assertEquals(0, (int) viewModel.colorPicker.getValue());
        assertEquals(1, (int) viewModel.priorityPicker.getValue());
        assertNotNull(viewModel.startYear.getValue());
        assertEquals(false, viewModel.isStartDatePickerShowing.getValue());
        assertEquals(false, viewModel.isEndDatePickerShowing.getValue());
        assertEquals(false, viewModel.isStartTimePickerShowing.getValue());
        assertEquals(false, viewModel.isEndTimePickerShowing.getValue());
    }

    @Test
    public void testToggleExpanded() {
        assertEquals(false, viewModel.isExpanded.getValue());
        viewModel.toggleExpanded();
        assertEquals(true, viewModel.isExpanded.getValue());
        viewModel.toggleExpanded();
        assertEquals(false, viewModel.isExpanded.getValue());
    }

    @Test
    public void testAddEvent() {
        viewModel.title.setValue("Test Title");
        viewModel.location.setValue("Test Location");
        viewModel.input.setValue("Test Input");
        viewModel.colorPicker.setValue(1);
        viewModel.priorityPicker.setValue(0);

        viewModel.addEvent();

        assertNotNull(viewModel.eventAddedEvent.getValue());
        assertTrue(viewModel.eventAddedEvent.getValue() > 0);
    }

}
