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

package com.martinatanasov.colornotebook;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.martinatanasov.colornotebook.dto.AddEvent;
import com.martinatanasov.colornotebook.services.EventService;
import com.martinatanasov.colornotebook.services.EventServiceImpl;
import com.martinatanasov.colornotebook.services.RescheduleWorkerService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

@RunWith(AndroidJUnit4.class)
public class NotificationPersistenceTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);
    private UiDevice device;
    private Context context;

    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testSoundNotificationPersistence() throws Exception {
        final long[] eventIdArr = new long[1];
        // 1. Create a sound event (even in the past) - run on main thread because of Toast
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            try (EventService eventService = new EventServiceImpl(context)) {
                Calendar past = Calendar.getInstance();
                past.add(Calendar.MINUTE, -10); // 10 minutes ago

                eventIdArr[0] = eventService.addEvent(new AddEvent(
                        "Persistence Test",
                        "Test Location",
                        "Test Node",
                        0, 0,
                        past.get(Calendar.YEAR), past.get(Calendar.MONTH), past.get(Calendar.DAY_OF_MONTH),
                        past.get(Calendar.HOUR_OF_DAY), past.get(Calendar.MINUTE),
                        past.get(Calendar.YEAR), past.get(Calendar.MONTH), past.get(Calendar.DAY_OF_MONTH),
                        past.get(Calendar.HOUR_OF_DAY), past.get(Calendar.MINUTE),
                        System.currentTimeMillis(), System.currentTimeMillis(),
                        0, 1, 0
                ));
            } catch (Exception e) {
                Log.e("NotificationPersistenceTest", "testSoundNotificationPersistence: ", e);
            }
        });

        long eventId = eventIdArr[0];
        assertTrue("Event creation failed", eventId != -1);

        try (EventService eventService = new EventServiceImpl(context)) {
            // 2. Run the RescheduleWorker
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RescheduleWorkerService.class).build();
            WorkManager.getInstance(context).enqueue(workRequest).getResult().get();

            // 3. Open notification shade and check for the notification
            device.openNotification();

            // Wait for the notification to appear
            boolean found = device.wait(Until.hasObject(By.text("Persistence Test")), 5000);

            // Cleanup: close shade and delete event
            device.pressBack();
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                eventService.deleteEventOnOneRow(String.valueOf(eventId));
            });

            assertTrue("Notification not found in shade after reschedule", found);
        }
    }

}
