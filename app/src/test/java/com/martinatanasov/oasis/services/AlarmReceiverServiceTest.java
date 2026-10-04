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

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AlarmReceiverServiceTest {

    private Context context;
    private AlarmReceiverService service;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        service = new AlarmReceiverService();
    }

    @Test
    public void testOnReceiveImportantPriority() {
        Intent intent = new Intent();
        intent.putExtra("id", "1234");
        intent.putExtra("title", "Important Alarm");
        intent.putExtra("node", "Alarm Note");
        intent.putExtra("priority", 0);
        intent.putExtra("color", 1);

        service.onReceive(context, intent);
    }

    @Test
    public void testOnReceiveRegularPriority() {
        Intent intent = new Intent();
        intent.putExtra("id", "1235");
        intent.putExtra("title", "Regular Alarm");
        intent.putExtra("node", "Regular Note");
        intent.putExtra("priority", 1);
        intent.putExtra("color", 2);

        service.onReceive(context, intent);
    }

    @Test
    public void testOnReceiveUnimportantPriority() {
        Intent intent = new Intent();
        intent.putExtra("id", "1236");
        intent.putExtra("title", "Unimportant Alarm");
        intent.putExtra("node", "Unimportant Note");
        intent.putExtra("priority", 2);
        intent.putExtra("color", 3);

        service.onReceive(context, intent);
    }

}
