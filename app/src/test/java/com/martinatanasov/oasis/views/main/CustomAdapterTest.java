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

package com.martinatanasov.oasis.views.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.test.core.app.ApplicationProvider;

import com.martinatanasov.oasis.dto.UserEventDTO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.time.Instant;
import java.util.Calendar;
import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
public class CustomAdapterTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testIsAlarmActive_futureAlarm_returnsTrue() {
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 1);

        UserEventDTO futureEvent = createEvent(
                1, // sound notification enabled
                future.get(Calendar.YEAR),
                (byte) future.get(Calendar.MONTH),
                (byte) future.get(Calendar.DAY_OF_MONTH),
                (byte) future.get(Calendar.HOUR_OF_DAY),
                (byte) future.get(Calendar.MINUTE)
        );

        assertTrue(futureEvent.isSoundAlarmActive());
    }

    @Test
    public void testIsAlarmActive_pastAlarm_returnsFalse() {
        Calendar past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -1);

        UserEventDTO pastEvent = createEvent(
                1, // sound notification enabled
                past.get(Calendar.YEAR),
                (byte) past.get(Calendar.MONTH),
                (byte) past.get(Calendar.DAY_OF_MONTH),
                (byte) past.get(Calendar.HOUR_OF_DAY),
                (byte) past.get(Calendar.MINUTE)
        );

        assertFalse(pastEvent.isSoundAlarmActive());
    }

    @Test
    public void testSoundAlarmIconVisibility_pastAlarmWithSoundOn_isGone() {
        Calendar past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -1);

        UserEventDTO pastEventWithSound = createEvent(
                1, // sound notification enabled
                past.get(Calendar.YEAR),
                (byte) past.get(Calendar.MONTH),
                (byte) past.get(Calendar.DAY_OF_MONTH),
                (byte) past.get(Calendar.HOUR_OF_DAY),
                (byte) past.get(Calendar.MINUTE)
        );

        CustomAdapter adapterWithEvent = new CustomAdapter(null, context, Collections.singletonList(pastEventWithSound));
        CustomAdapter.MyViewHolder holder = createViewHolder();

        adapterWithEvent.onBindViewHolder(holder, 0);

        assertEquals(View.GONE, holder.soundNotificationsIcon.getVisibility());
    }

    @Test
    public void testSoundAlarmIconVisibility_futureAlarmWithSoundOn_isVisible() {
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 1);

        UserEventDTO futureEventWithSound = createEvent(
                1, // sound notification enabled
                future.get(Calendar.YEAR),
                (byte) future.get(Calendar.MONTH),
                (byte) future.get(Calendar.DAY_OF_MONTH),
                (byte) future.get(Calendar.HOUR_OF_DAY),
                (byte) future.get(Calendar.MINUTE)
        );

        CustomAdapter adapterWithEvent = new CustomAdapter(null, context, Collections.singletonList(futureEventWithSound));
        CustomAdapter.MyViewHolder holder = createViewHolder();

        adapterWithEvent.onBindViewHolder(holder, 0);

        assertEquals(View.VISIBLE, holder.soundNotificationsIcon.getVisibility());
    }

    @Test
    public void testSoundAlarmIconVisibility_futureAlarmWithSoundOff_isGone() {
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 1);

        UserEventDTO futureEventWithSoundOff = createEvent(
                0, // sound notification disabled
                future.get(Calendar.YEAR),
                (byte) future.get(Calendar.MONTH),
                (byte) future.get(Calendar.DAY_OF_MONTH),
                (byte) future.get(Calendar.HOUR_OF_DAY),
                (byte) future.get(Calendar.MINUTE)
        );

        CustomAdapter adapterWithEvent = new CustomAdapter(null, context, Collections.singletonList(futureEventWithSoundOff));
        CustomAdapter.MyViewHolder holder = createViewHolder();

        adapterWithEvent.onBindViewHolder(holder, 0);

        assertEquals(View.GONE, holder.soundNotificationsIcon.getVisibility());
    }

    private CustomAdapter.MyViewHolder createViewHolder() {
        View itemView = new View(context);
        CustomAdapter.MyViewHolder holder = new CustomAdapter.MyViewHolder(itemView);
        holder.txtEventTitle = new TextView(context);
        holder.txtNode = new TextView(context);
        holder.mainLayout = new LinearLayout(context);
        holder.cardViewEvent = new CardView(context);
        holder.allDayIcon = new ImageView(context);
        holder.soundNotificationsIcon = new ImageView(context);
        holder.silentNotificationIcon = new ImageView(context);
        return holder;
    }

    private UserEventDTO createEvent(int soundNotification, int year, byte month, byte day, byte hour, byte minute) {
        return new UserEventDTO(
                "1", "Test Title", "Test Location", "Test Note", 0, 0,
                year, year, 0, soundNotification, 0,
                month, day, hour, minute,
                month, day, hour, minute,
                Instant.now(), Instant.now(), 0
        );
    }
}
