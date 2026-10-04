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

import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestListenableWorkerBuilder;
import androidx.work.testing.WorkManagerTestInitHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SilentNotificationWorkerTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        WorkManagerTestInitHelper.initializeTestWorkManager(context);
    }

    @After
    public void tearDown() {
        WorkManagerTestInitHelper.closeWorkDatabase();
    }

    @Test
    public void testWorkerDoWork() {
        Data inputData = new Data.Builder()
                .putString("id", "500")
                .putString("title", "Silent Title")
                .putString("note", "Silent Note long description text to test stripNote method properly")
                .putInt("color", 2)
                .putInt("priority", 1)
                .build();

        SilentNotificationWorker worker = TestListenableWorkerBuilder.from(context, SilentNotificationWorker.class)
                .setInputData(inputData)
                .build();

        ListenableWorker.Result result = worker.doWork();
        assertNotNull(result);
    }

    @Test
    public void testScheduleAndCancelSilentNotifications() {
        SilentNotificationWorker.scheduleSilentNotification(context, "900", "Test", "Note", 1, 0, 1000L);
        SilentNotificationWorker.cancelSilentNotification(context, "900");
        SilentNotificationWorker.cancelAllSilentNotifications(context);
    }

}
