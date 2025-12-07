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

package com.martinatanasov.colornotebook.dto;

public record UserEvent(String txtEventId, String txtEventTitle, String txtEventLocation,
                        String txtNode, int int_color_picker, int int_avatar_picker,
                        int int_start_year, int int_end_year, int int_all_day,
                        int int_sound_notifications, int int_silent_notifications,
                        byte byte_start_month, byte byte_start_day, byte byte_start_hour,
                        byte byte_start_minutes, byte byte_end_month, byte byte_end_day,
                        byte byte_end_hour, byte byte_end_minutes, long long_created_date,
                        long long_modified_date) {

}
