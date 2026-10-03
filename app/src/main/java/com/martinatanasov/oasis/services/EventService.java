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

import android.database.Cursor;

import com.martinatanasov.oasis.models.AddEventDTO;
import com.martinatanasov.oasis.models.UpdateEventDTO;
import com.martinatanasov.oasis.models.UserEventDTO;

import java.util.List;

public interface EventService extends AutoCloseable {

    long addEvent(AddEventDTO addEventDTO);

    Cursor readAllEvents();

    List<UserEventDTO> getUserEventDto();

    void updateEvent(UpdateEventDTO updateEventDTO);

    void deleteEventOnOneRow(String row_id);

    void deleteAllEvents();

    void removeSoundNotification(String row_id);

    void removeSilentNotification(String row_id);

    @Override
    void close();
}
