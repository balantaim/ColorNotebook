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

public record UpdateEvent(String row_id, String title, String location, String node, int color,
                          int avatar, int startYear,
                          int startMonth, int startDay, int startHour, int startMinutes,
                          int endYear, int endMonth, int endDay, int endHour, int endMinutes,
                          long createdDate, long modifiedDate,
                          int allDay, int soundNotifications, int silentNotifications) {

}
