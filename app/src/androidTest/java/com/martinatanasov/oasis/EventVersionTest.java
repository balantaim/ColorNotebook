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

package com.martinatanasov.oasis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.martinatanasov.oasis.models.AddEventDTO;
import com.martinatanasov.oasis.models.UpdateEventDTO;
import com.martinatanasov.oasis.models.UserEventDTO;
import com.martinatanasov.oasis.services.EventService;
import com.martinatanasov.oasis.services.EventServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class EventVersionTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            try (EventService eventService = new EventServiceImpl(context)) {
                eventService.deleteAllEvents();
            }
        });
    }

    @Test
    @SuppressLint("NewApi")
    public void testVersionIncrementsOnUpdate() {
        // 1. Add event
        final long[] idArr = new long[1];
        AddEventDTO addDto = new AddEventDTO(
                "Version Test", "Location", "Input", 0, 0,
                2023, 10, 27, 10, 0,
                2023, 10, 27, 11, 0,
                Instant.now(), Instant.now(),
                0, 1, 1, 0
        );
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            try (EventService eventService = new EventServiceImpl(context)) {
                idArr[0] = eventService.addEvent(addDto);
            }
        });
        long id = idArr[0];

        // 2. Verify initial version is 0
        try (EventService eventService = new EventServiceImpl(context)) {
            List<UserEventDTO> events = eventService.getUserEventDto();
            UserEventDTO event = events.stream().filter(e -> e.txtEventId().equals(String.valueOf(id))).findFirst().orElse(null);
            assertNotNull(event);
            assertEquals(0, event.int_version());

            // 3. Update event
            UpdateEventDTO updateDto = new UpdateEventDTO(
                    String.valueOf(id), "Updated Title", "Location", "Input", 0, 0,
                    2023, 10, 27, 10, 0,
                    2023, 10, 27, 11, 0,
                    event.instant_created_date(), Instant.now(),
                    0, 1, 1, event.int_version()
            );

            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                try (EventService es = new EventServiceImpl(context)) {
                    es.updateEvent(updateDto);
                }
            });

            // 4. Verify version is incremented to 1
            events = eventService.getUserEventDto();
            event = events.stream().filter(e -> e.txtEventId().equals(String.valueOf(id))).findFirst().orElse(null);
            assertNotNull(event);
            assertEquals(1, event.int_version());
        }
    }

    @Test
    @SuppressLint("NewApi")
    public void testVersionDoesNotIncrementOnNotificationRemoval() {
        // 1. Add event
        final long[] idArr = new long[1];
        AddEventDTO addDto = new AddEventDTO(
                "Notification Test", "Location", "Input", 0, 0,
                2023, 10, 27, 10, 0,
                2023, 10, 27, 11, 0,
                Instant.now(), Instant.now(),
                0, 1, 1, 0
        );
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            try (EventService eventService = new EventServiceImpl(context)) {
                idArr[0] = eventService.addEvent(addDto);
            }
        });
        String idStr = String.valueOf(idArr[0]);

        try (EventService eventService = new EventServiceImpl(context)) {
            // 2. Verify initial version is 0
            List<UserEventDTO> events = eventService.getUserEventDto();
            UserEventDTO event = events.stream().filter(e -> e.txtEventId().equals(idStr)).findFirst().orElse(null);
            assertNotNull(event);
            assertEquals(0, event.int_version());

            // 3. Remove sound notification
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                try (EventService es = new EventServiceImpl(context)) {
                    es.removeSoundNotification(idStr);
                }
            });

            // 4. Verify version remains 0
            events = eventService.getUserEventDto();
            event = events.stream().filter(e -> e.txtEventId().equals(idStr)).findFirst().orElse(null);
            assertNotNull(event);
            assertEquals(0, event.int_version());

            // 5. Remove silent notification
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                try (EventService es = new EventServiceImpl(context)) {
                    es.removeSilentNotification(idStr);
                }
            });

            // 6. Verify version still remains 0
            events = eventService.getUserEventDto();
            event = events.stream().filter(e -> e.txtEventId().equals(idStr)).findFirst().orElse(null);
            assertNotNull(event);
            assertEquals(0, event.int_version());
        }
    }

    @Test
    @SuppressLint("NewApi")
    public void testOptimisticLockingPreventsUpdateWithWrongVersion() {
        // 1. Add event
        final long[] idArr = new long[1];
        AddEventDTO addDto = new AddEventDTO(
                "Locking Test", "Location", "Input", 0, 0,
                2023, 10, 27, 10, 0,
                2023, 10, 27, 11, 0,
                Instant.now(), Instant.now(),
                0, 0, 0, 0
        );
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            try (EventService eventService = new EventServiceImpl(context)) {
                idArr[0] = eventService.addEvent(addDto);
            }
        });
        String idStr = String.valueOf(idArr[0]);

        try (EventService eventService = new EventServiceImpl(context)) {
            // 2. Attempt update with wrong version (e.g., version 1 when DB has 0)
            UpdateEventDTO wrongVersionDto = new UpdateEventDTO(
                    idStr, "Stale Update", "Location", "Input", 0, 0,
                    2023, 10, 27, 10, 0,
                    2023, 10, 27, 11, 0,
                    Instant.now(), Instant.now(),
                    0, 0, 0, 1 // Wrong version
            );

            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                try (EventService es = new EventServiceImpl(context)) {
                    es.updateEvent(wrongVersionDto);
                }
            });

            // 3. Verify record was NOT updated and version is still 0
            List<UserEventDTO> events = eventService.getUserEventDto();
            UserEventDTO event = events.stream().filter(e -> e.txtEventId().equals(idStr)).findFirst().orElse(null);
            assertNotNull(event);
            assertEquals(0, event.int_version());
            assertEquals("Locking Test", event.txtEventTitle()); // Title should remain unchanged
        }
    }

}
