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

package com.martinatanasov.oasis.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.martinatanasov.oasis.dto.AddEventDTO;
import com.martinatanasov.oasis.dto.UpdateEventDTO;
import com.martinatanasov.oasis.dto.UserEventDTO;
import com.martinatanasov.oasis.repositories.EventDatabaseRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class EventServiceImpl implements EventService {

    private final EventDatabaseRepository database;

    public EventServiceImpl(Context context) {
        database = new EventDatabaseRepository(context);
    }

    @Override
    public long addEvent(AddEventDTO event) {
        return database.addEvent(event);
    }

    @Override
    public Cursor readAllEvents() {
        return database.readAllEvents();
    }

    @Override
    @SuppressLint("NewApi")
    public List<UserEventDTO> getUserEventDto() {
        List<UserEventDTO> userEventDTO = new ArrayList<>();
        try (Cursor cursor = readAllEvents()) {
            if (cursor == null || cursor.getCount() == 0) {
                return userEventDTO;
            } else {
                while (cursor.moveToNext()) {
                    userEventDTO.add(new UserEventDTO(
                            cursor.getString(0), //id
                            cursor.getString(1), //title
                            cursor.getString(2), //location
                            cursor.getString(3), //event_node
                            Integer.parseInt(cursor.getString(4)), //color
                            Integer.parseInt(cursor.getString(5)), //avatar
                            Integer.parseInt(cursor.getString(6)), //start_year
                            Integer.parseInt(cursor.getString(11)), //end_year
                            Integer.parseInt(cursor.getString(18)), //all_day
                            Integer.parseInt(cursor.getString(19)), //sound_notifications
                            Integer.parseInt(cursor.getString(20)), //silent_notifications
                            Byte.parseByte(cursor.getString(7)), //start_mount
                            Byte.parseByte(cursor.getString(8)), //start_day
                            Byte.parseByte(cursor.getString(9)), //start_hour
                            Byte.parseByte(cursor.getString(10)), //start_minutes
                            Byte.parseByte(cursor.getString(12)), //end_mount
                            Byte.parseByte(cursor.getString(13)), //end_day
                            Byte.parseByte(cursor.getString(14)), //end_hour
                            Byte.parseByte(cursor.getString(15)), //end_minutes
                            Instant.ofEpochMilli(Long.parseLong(cursor.getString(16))), //create_date
                            Instant.ofEpochMilli(Long.parseLong(cursor.getString(17))), //modified_date
                            Integer.parseInt(cursor.getString(21)) //version
                    ));
                }
            }
        }
        return userEventDTO;
    }

    @Override
    public void updateEvent(UpdateEventDTO event) {
        database.updateEvent(event);
    }

    @Override
    public void deleteEventOnOneRow(String row_id) {
        database.deleteEventOnOneRow(row_id);
    }

    @Override
    public void deleteAllEvents() {
        database.deleteAllEvents();
    }

    @Override
    public void removeSoundNotification(String row_id) {
        database.removeSoundNotification(row_id);
    }

    @Override
    public void removeSilentNotification(String row_id) {
        database.removeSilentNotification(row_id);
    }

    @Override
    public void close() {
        if (database != null) {
            database.close();
        }
    }

}
