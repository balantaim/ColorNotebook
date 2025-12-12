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

package com.martinatanasov.colornotebook.services;

import android.content.Context;
import android.database.Cursor;

import com.martinatanasov.colornotebook.dto.AddEvent;
import com.martinatanasov.colornotebook.dto.UpdateEvent;
import com.martinatanasov.colornotebook.dto.UserEvent;
import com.martinatanasov.colornotebook.repositories.EventDatabaseRepository;

import java.util.ArrayList;
import java.util.List;

public class EventServiceImpl implements EventService {

    private final EventDatabaseRepository database;

    public EventServiceImpl(Context context) {
        database = new EventDatabaseRepository(context);
    }

    @Override
    public void addEvent(AddEvent event) {
        database.addEvent(event);
    }

    @Override
    public Cursor readAllEvents() {
        return database.readAllEvents();
    }

    @Override
    public List<UserEvent> getUserEventDto() {
        List<UserEvent> userEvent = new ArrayList<>();
        try (Cursor cursor = readAllEvents()) {
            if (cursor.getCount() == 0) {
                return userEvent;
            } else {
                while (cursor.moveToNext()) {
                    userEvent.add(new UserEvent(
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
                            Long.parseLong(cursor.getString(16)), //create_date
                            Long.parseLong(cursor.getString(17)) //modified_date
                    ));
                }
            }
        }
        return userEvent;
    }

    @Override
    public void updateEvent(UpdateEvent event) {
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

}
