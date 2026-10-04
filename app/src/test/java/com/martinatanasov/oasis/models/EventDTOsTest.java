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

package com.martinatanasov.oasis.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.Instant;
import java.util.Calendar;

public class EventDTOsTest {

    @Test
    public void testAddEventDTO() {
        Instant now = Instant.now();
        AddEventDTO dto = new AddEventDTO(
                "Title", "Location", "Input", 1, 2,
                2024, 5, 15, 10, 30,
                2024, 5, 15, 11, 30,
                now, now, 1, 1, 0, 0
        );

        assertEquals("Title", dto.title());
        assertEquals("Location", dto.location());
        assertEquals("Input", dto.input());
        assertEquals(1, dto.color());
        assertEquals(2, dto.avatar());
        assertEquals(2024, dto.startYear());
        assertEquals(5, dto.startMonth());
        assertEquals(15, dto.startDay());
        assertEquals(10, dto.startHour());
        assertEquals(30, dto.startMinutes());
        assertEquals(2024, dto.endYear());
        assertEquals(5, dto.endMonth());
        assertEquals(15, dto.endDay());
        assertEquals(11, dto.endHour());
        assertEquals(30, dto.endMinutes());
        assertEquals(now, dto.createdDate());
        assertEquals(now, dto.modifiedDate());
        assertEquals(1, dto.allDay());
        assertEquals(1, dto.soundNotifications());
        assertEquals(0, dto.silentNotifications());
        assertEquals(0, dto.version());
    }

    @Test
    public void testUpdateEventDTO() {
        Instant now = Instant.now();
        UpdateEventDTO dto = new UpdateEventDTO(
                "10", "Title", "Location", "Node", 1, 2,
                2024, 5, 15, 10, 30,
                2024, 5, 15, 11, 30,
                now, now, 0, 1, 1, 3
        );

        assertEquals("10", dto.row_id());
        assertEquals("Title", dto.title());
        assertEquals("Location", dto.location());
        assertEquals("Node", dto.node());
        assertEquals(1, dto.color());
        assertEquals(2, dto.avatar());
        assertEquals(3, dto.version());
    }

    @Test
    public void testUserEventDTOIsSoundAlarmActive() {
        Instant now = Instant.now();
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 1);

        UserEventDTO futureEventWithSound = new UserEventDTO(
                "1", "Title", "Loc", "Node", 0, 0,
                future.get(Calendar.YEAR), future.get(Calendar.YEAR), 0, 1, 0,
                (byte) future.get(Calendar.MONTH), (byte) future.get(Calendar.DAY_OF_MONTH),
                (byte) future.get(Calendar.HOUR_OF_DAY), (byte) future.get(Calendar.MINUTE),
                (byte) future.get(Calendar.MONTH), (byte) future.get(Calendar.DAY_OF_MONTH),
                (byte) future.get(Calendar.HOUR_OF_DAY), (byte) future.get(Calendar.MINUTE),
                now, now, 0
        );

        assertTrue(futureEventWithSound.isSoundAlarmActive());
        assertTrue(futureEventWithSound.isSoundAlarmActive(Calendar.getInstance()));

        UserEventDTO futureEventNoSound = new UserEventDTO(
                "2", "Title", "Loc", "Node", 0, 0,
                future.get(Calendar.YEAR), future.get(Calendar.YEAR), 0, 0, 0,
                (byte) future.get(Calendar.MONTH), (byte) future.get(Calendar.DAY_OF_MONTH),
                (byte) future.get(Calendar.HOUR_OF_DAY), (byte) future.get(Calendar.MINUTE),
                (byte) future.get(Calendar.MONTH), (byte) future.get(Calendar.DAY_OF_MONTH),
                (byte) future.get(Calendar.HOUR_OF_DAY), (byte) future.get(Calendar.MINUTE),
                now, now, 0
        );

        assertFalse(futureEventNoSound.isSoundAlarmActive());

        Calendar past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -1);

        UserEventDTO pastEventWithSound = new UserEventDTO(
                "3", "Title", "Loc", "Node", 0, 0,
                past.get(Calendar.YEAR), past.get(Calendar.YEAR), 0, 1, 0,
                (byte) past.get(Calendar.MONTH), (byte) past.get(Calendar.DAY_OF_MONTH),
                (byte) past.get(Calendar.HOUR_OF_DAY), (byte) past.get(Calendar.MINUTE),
                (byte) past.get(Calendar.MONTH), (byte) past.get(Calendar.DAY_OF_MONTH),
                (byte) past.get(Calendar.HOUR_OF_DAY), (byte) past.get(Calendar.MINUTE),
                now, now, 0
        );

        assertFalse(pastEventWithSound.isSoundAlarmActive());
        assertNotNull(futureEventWithSound.txtEventId());
        assertNotNull(futureEventWithSound.txtEventTitle());
        assertNotNull(futureEventWithSound.txtEventLocation());
        assertNotNull(futureEventWithSound.txtNode());
        assertNotNull(futureEventWithSound.instant_created_date());
        assertNotNull(futureEventWithSound.instant_modified_date());
    }

}
