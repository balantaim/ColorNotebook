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

import com.martinatanasov.oasis.models.AddEventDTO;
import com.martinatanasov.oasis.models.UserEventDTO;
import com.martinatanasov.oasis.services.EventService;
import com.martinatanasov.oasis.services.EventServiceImpl;
import com.martinatanasov.oasis.views.main.OrderFilter;
import com.martinatanasov.oasis.views.main.PriorityFilter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import androidx.work.testing.WorkManagerTestInitHelper;

import java.time.Instant;
import java.util.Calendar;
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
        WorkManagerTestInitHelper.initializeTestWorkManager(application);
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
        UserEventDTO oldImportant = new UserEventDTO("1", "Old Important", "", "", 0, 0, 2023, 2023, 0, 0, 0, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1, (byte) 1, (byte) 11, (byte) 0, Instant.ofEpochMilli(1000L), Instant.ofEpochMilli(1000L), 0);
        UserEventDTO newUnimportant = new UserEventDTO("2", "New Unimportant", "", "", 0, 2, 2023, 2023, 0, 0, 0, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1, (byte) 1, (byte) 11, (byte) 0, Instant.ofEpochMilli(2000L), Instant.ofEpochMilli(2000L), 0);

        viewModel.filteredEvents.observeForever(events -> {
        }); // Make it active

        viewModel.setEvents(List.of(oldImportant, newUnimportant));
        viewModel.setOrderFilter(OrderFilter.DATE); // Newest first
        viewModel.setPriorityFilter(PriorityFilter.NONE);

        List<UserEventDTO> filtered = viewModel.filteredEvents.getValue();
        assertNotNull(filtered);
        assertEquals(2, filtered.size());
        // With PriorityFilter.NONE, it should ONLY sort by date. Newest (2000L) should be first.
        assertEquals("New Unimportant", filtered.get(0).txtEventTitle());
        assertEquals("Old Important", filtered.get(1).txtEventTitle());
    }

    @Test
    public void testPriorityFilteringSorting() {
        UserEventDTO oldImportant = new UserEventDTO("1", "Old Important", "", "", 0, 0, 2023, 2023, 0, 0, 0, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1, (byte) 1, (byte) 11, (byte) 0, Instant.ofEpochMilli(1000L), Instant.ofEpochMilli(1000L), 0);
        UserEventDTO newUnimportant = new UserEventDTO("2", "New Unimportant", "", "", 0, 2, 2023, 2023, 0, 0, 0, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1, (byte) 1, (byte) 11, (byte) 0, Instant.ofEpochMilli(2000L), Instant.ofEpochMilli(2000L), 0);
        UserEventDTO oldUnimportant = new UserEventDTO("3", "Old Unimportant", "", "", 0, 2, 2023, 2023, 0, 0, 0, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1, (byte) 1, (byte) 11, (byte) 0, Instant.ofEpochMilli(500L), Instant.ofEpochMilli(500L), 0);

        viewModel.filteredEvents.observeForever(events -> {
        }); // Make it active

        viewModel.setEvents(List.of(oldImportant, newUnimportant, oldUnimportant));
        viewModel.setOrderFilter(OrderFilter.DATE); // Newest first
        viewModel.setPriorityFilter(PriorityFilter.UNIMPORTANT); // Prioritize Unimportant

        List<UserEventDTO> filtered = viewModel.filteredEvents.getValue();
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
    public void testSoundNotificationsCount() {
        // Clear database first
        viewModel.deleteBatch();

        Calendar futureCalendar = Calendar.getInstance();
        futureCalendar.add(Calendar.YEAR, 1);
        int year = futureCalendar.get(Calendar.YEAR);

        // Add one event with sound notification
        AddEventDTO eventWithSound = new AddEventDTO(
                "Title 1", "Location 1", "Node 1", 0, 0,
                year, 1, 1, 10, 0,
                year, 1, 1, 11, 0,
                Instant.now(), Instant.now(),
                0, 1, 0, 0
        );

        // Add one event without sound notification
        AddEventDTO eventWithoutSound = new AddEventDTO(
                "Title 2", "Location 2", "Node 2", 0, 0,
                year, 1, 1, 10, 0,
                year, 1, 1, 11, 0,
                Instant.now(), Instant.now(),
                0, 0, 0, 0
        );

        // We need to use EventService to add them to the real DB used by ViewModel
        EventService eventService = new EventServiceImpl(application);
        eventService.addEvent(eventWithSound);
        eventService.addEvent(eventWithoutSound);

        // Load data in ViewModel
        viewModel.loadData();

        assertEquals(1, (int) viewModel.soundNotificationsCount.getValue());
        assertEquals(2, viewModel.events.getValue().size());
    }

    @Test
    public void testActiveAlarmsCountOnlyFuture() {
        // This test will verify if "Active alarms" only counts future alarms
        viewModel.deleteBatch();

        Calendar pastCalendar = Calendar.getInstance();
        pastCalendar.add(Calendar.HOUR, -1);

        AddEventDTO pastEvent = new AddEventDTO(
                "Past Event", "", "", 0, 0,
                pastCalendar.get(Calendar.YEAR), pastCalendar.get(Calendar.MONTH), pastCalendar.get(Calendar.DAY_OF_MONTH),
                pastCalendar.get(Calendar.HOUR_OF_DAY), pastCalendar.get(Calendar.MINUTE),
                pastCalendar.get(Calendar.YEAR), pastCalendar.get(Calendar.MONTH), pastCalendar.get(Calendar.DAY_OF_MONTH),
                pastCalendar.get(Calendar.HOUR_OF_DAY) + 1, pastCalendar.get(Calendar.MINUTE),
                Instant.now(), Instant.now(),
                0, 1, 0, 0
        );

        Calendar futureCalendar = Calendar.getInstance();
        futureCalendar.add(Calendar.HOUR, 1);

        AddEventDTO futureEvent = new AddEventDTO(
                "Future Event", "", "", 0, 0,
                futureCalendar.get(Calendar.YEAR), futureCalendar.get(Calendar.MONTH), futureCalendar.get(Calendar.DAY_OF_MONTH),
                futureCalendar.get(Calendar.HOUR_OF_DAY), futureCalendar.get(Calendar.MINUTE),
                futureCalendar.get(Calendar.YEAR), futureCalendar.get(Calendar.MONTH), futureCalendar.get(Calendar.DAY_OF_MONTH),
                futureCalendar.get(Calendar.HOUR_OF_DAY) + 1, futureCalendar.get(Calendar.MINUTE),
                Instant.now(), Instant.now(),
                0, 1, 0, 0
        );

        EventService eventService = new EventServiceImpl(application);
        eventService.addEvent(pastEvent);
        eventService.addEvent(futureEvent);

        viewModel.loadData();

        // If "Active alarms" means ONLY future ones, it should be 1.
        assertEquals(1, (int) viewModel.soundNotificationsCount.getValue());
    }

}
