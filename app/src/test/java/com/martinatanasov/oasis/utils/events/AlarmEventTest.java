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

package com.martinatanasov.oasis.utils.events;

import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Calendar;

@RunWith(RobolectricTestRunner.class)
public class AlarmEventTest {

    private AlarmEvent alarmEvent;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        alarmEvent = new AlarmEvent(context);
    }

    @Test
    public void testSetUpAlarmAndCancelAlarm() {
        Calendar future = Calendar.getInstance();
        future.add(Calendar.HOUR, 1);

        alarmEvent.setUpAlarm("101", "Test Title", "Test Note", future, 0, 0);
        alarmEvent.setUpAlarm("102", "Test Title 2", "Test Note 2", future, 1, 1);
        alarmEvent.setUpAlarm("103", "Test Title 3", "Test Note 3", future, 2, 2);

        alarmEvent.cancelAlarm("101");
        alarmEvent.cancelAlarm("102");
        alarmEvent.cancelAlarm("103");
        alarmEvent.cancelAllAlarms();
    }

    @Test
    public void testNextAlarmTriggerTime() {
        long triggerTime = alarmEvent.nextAlarmTriggerTime();
        assertTrue(triggerTime >= 0);
    }

}
