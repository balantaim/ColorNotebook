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
import androidx.work.testing.WorkManagerTestInitHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BroadcastReceiverServiceTest {

    private Context context;
    private BroadcastReceiverService receiver;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        WorkManagerTestInitHelper.initializeTestWorkManager(context);
        receiver = new BroadcastReceiverService();
    }

    @After
    public void tearDown() {
        WorkManagerTestInitHelper.closeWorkDatabase();
    }

    @Test
    public void testOnReceiveBootCompleted() {
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        receiver.onReceive(context, intent);
    }

    @Test
    public void testOnReceiveOtherAction() {
        Intent intent = new Intent("UNHANDLED_ACTION");
        receiver.onReceive(context, intent);
    }

}
