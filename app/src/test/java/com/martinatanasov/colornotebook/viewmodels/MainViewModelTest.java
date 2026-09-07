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

import com.martinatanasov.colornotebook.dto.UserEvent;
import com.martinatanasov.colornotebook.views.main.OrderFilter;
import com.martinatanasov.colornotebook.views.main.PriorityFilter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private MainViewModel viewModel;
    private Application application;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
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
        // Verify default filters
        assertEquals(OrderFilter.DATE, viewModel.orderFilter.getValue());
        assertEquals(PriorityFilter.NONE, viewModel.priorityFilter.getValue());
    }

    @Test
    public void testPriorityNoneSorting() {
        UserEvent oldImportant = new UserEvent("1", "Old Important", "", "", 0, 0, 2023, 2023, 0, 0, 0, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1, (byte) 1, (byte) 11, (byte) 0, 1000L, 1000L);
        UserEvent newUnimportant = new UserEvent("2", "New Unimportant", "", "", 0, 2, 2023, 2023, 0, 0, 0, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1, (byte) 1, (byte) 11, (byte) 0, 2000L, 2000L);

        viewModel.filteredEvents.observeForever(events -> {
        }); // Make it active

        viewModel.setEvents(List.of(oldImportant, newUnimportant));
        viewModel.setOrderFilter(OrderFilter.DATE); // Newest first
        viewModel.setPriorityFilter(PriorityFilter.NONE);

        List<UserEvent> filtered = viewModel.filteredEvents.getValue();
        assertNotNull(filtered);
        assertEquals(2, filtered.size());
        // With PriorityFilter.NONE, it should ONLY sort by date. Newest (2000L) should be first.
        assertEquals("New Unimportant", filtered.get(0).txtEventTitle());
        assertEquals("Old Important", filtered.get(1).txtEventTitle());
    }

    @Test
    public void testPriorityFilteringSorting() {
        UserEvent oldImportant = new UserEvent("1", "Old Important", "", "", 0, 0, 2023, 2023, 0, 0, 0, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1, (byte) 1, (byte) 11, (byte) 0, 1000L, 1000L);
        UserEvent newUnimportant = new UserEvent("2", "New Unimportant", "", "", 0, 2, 2023, 2023, 0, 0, 0, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1, (byte) 1, (byte) 11, (byte) 0, 2000L, 2000L);
        UserEvent oldUnimportant = new UserEvent("3", "Old Unimportant", "", "", 0, 2, 2023, 2023, 0, 0, 0, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1, (byte) 1, (byte) 11, (byte) 0, 500L, 500L);

        viewModel.filteredEvents.observeForever(events -> {
        }); // Make it active

        viewModel.setEvents(List.of(oldImportant, newUnimportant, oldUnimportant));
        viewModel.setOrderFilter(OrderFilter.DATE); // Newest first
        viewModel.setPriorityFilter(PriorityFilter.UNIMPORTANT); // Prioritize Unimportant

        List<UserEvent> filtered = viewModel.filteredEvents.getValue();
        assertNotNull(filtered);
        // Should have: New Unimportant (2000), Old Unimportant (500), Old Important (1000)
        assertEquals("New Unimportant", filtered.get(0).txtEventTitle());
        assertEquals("Old Unimportant", filtered.get(1).txtEventTitle());
        assertEquals("Old Important", filtered.get(2).txtEventTitle());
    }

    @Test
    public void testFilterPersistence() {
        // Set new filters
        viewModel.setOrderFilter(OrderFilter.A_Z);
        viewModel.setPriorityFilter(PriorityFilter.IMPORTANT);

        assertEquals(OrderFilter.A_Z, viewModel.orderFilter.getValue());
        assertEquals(PriorityFilter.IMPORTANT, viewModel.priorityFilter.getValue());

        // Create a new ViewModel to see if it loads the saved values
        MainViewModel newViewModel = new MainViewModel(application);
        assertEquals(OrderFilter.A_Z, newViewModel.orderFilter.getValue());
        assertEquals(PriorityFilter.IMPORTANT, newViewModel.priorityFilter.getValue());
    }

    @Test
    public void testSearchFiltering() {
        assertNotNull(viewModel.filteredEvents.getValue());
        assertTrue(viewModel.filteredEvents.getValue().isEmpty());

        viewModel.setSearchQuery("test");
        assertEquals("test", viewModel.searchQuery.getValue());
    }

}
