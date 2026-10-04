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
import com.martinatanasov.oasis.services.EventService;
import com.martinatanasov.oasis.services.EventServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.time.Instant;

@RunWith(RobolectricTestRunner.class)
public class UpdateViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private UpdateViewModel viewModel;
    private Application application;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        viewModel = new UpdateViewModel(application);
    }

    @After
    public void tearDown() {
        if (viewModel != null) {
            viewModel.onCleared();
        }
    }

    @Test
    public void testInitialState() {
        assertEquals("", viewModel.id.getValue());
        assertEquals("", viewModel.title.getValue());
        assertEquals(0, (int) viewModel.colorPicker.getValue());
        assertEquals(0, (int) viewModel.priorityPicker.getValue());
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
    }

    @Test
    public void testUpdateAndDeleteEvent() {
        long id;
        try (EventService eventService = new EventServiceImpl(application)) {
            id = eventService.addEvent(new AddEventDTO(
                    "Title", "Loc", "Input", 0, 0,
                    2024, 1, 1, 10, 0,
                    2024, 1, 1, 11, 0,
                    Instant.now(), Instant.now(), 0, 0, 0, 0
            ));
        }

        viewModel.id.setValue(String.valueOf(id));
        viewModel.title.setValue("Updated Title");
        viewModel.location.setValue("Updated Loc");
        viewModel.input.setValue("Updated Input");
        viewModel.version.setValue(0);

        viewModel.updateEvent();
        assertNotNull(viewModel.eventUpdatedEvent.getValue());
        assertTrue(viewModel.eventUpdatedEvent.getValue());

        viewModel.deleteEvent();
        assertNotNull(viewModel.eventDeletedEvent.getValue());
        assertTrue(viewModel.eventDeletedEvent.getValue());
    }

}
