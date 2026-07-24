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
import static org.junit.Assert.assertTrue;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private MainViewModel viewModel;

    @Before
    public void setUp() {
        Application application = ApplicationProvider.getApplicationContext();
        viewModel = new MainViewModel(application);
    }

    @Test
    public void testInitialState() {
        assertNotNull(viewModel.events.getValue());
        assertTrue(viewModel.events.getValue().isEmpty());
        assertEquals(0, (int) viewModel.importantCount.getValue());
        assertEquals(0, (int) viewModel.regularCount.getValue());
        assertEquals(0, (int) viewModel.unimportantCount.getValue());
        assertTrue(viewModel.isDataEmpty.getValue());
    }

    @Test
    public void testLoadData() {
        viewModel.loadData();
        // Since database is empty in tests, it should still be empty
        assertTrue(viewModel.events.getValue().isEmpty());
        assertTrue(viewModel.isDataEmpty.getValue());
    }

    @Test
    public void testSearchFiltering() {
        assertNotNull(viewModel.filteredEvents.getValue());
        assertTrue(viewModel.filteredEvents.getValue().isEmpty());

        viewModel.setSearchQuery("test");
        assertEquals("test", viewModel.searchQuery.getValue());
    }

}
