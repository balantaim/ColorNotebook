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

package com.martinatanasov.oasis;

import static org.junit.Assert.assertTrue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

import com.martinatanasov.oasis.dto.AddEventDTO;
import com.martinatanasov.oasis.services.AlarmReceiverService;
import com.martinatanasov.oasis.services.EventService;
import com.martinatanasov.oasis.services.EventServiceImpl;
import com.martinatanasov.oasis.services.RescheduleWorkerService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.Calendar;

@RunWith(AndroidJUnit4.class)
public class NotificationPersistenceTest {

    @Rule
    public GrantPermissionRule permissionRule = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)
            : GrantPermissionRule.grant();

    private UiDevice device;
    private Context context;

    @Before
    public void setUp() throws Exception {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        context = ApplicationProvider.getApplicationContext();
        device.wakeUp();
        device.executeShellCommand("wm dismiss-keyguard");
        device.pressHome();
    }

    @Test
    @SuppressLint("NewApi")
    public void testSoundNotificationPersistence() throws Exception {
        final long[] eventIdArr = new long[1];
        // 1. Create a sound event set 1 minute in future (since getStartCalendar truncates seconds)
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            try (EventService eventService = new EventServiceImpl(context)) {
                Calendar future = Calendar.getInstance();
                future.add(Calendar.MINUTE, 1);

                eventIdArr[0] = eventService.addEvent(new AddEventDTO(
                        "Persistence Test",
                        "Test Location",
                        "Test Node",
                        0, 0,
                        future.get(Calendar.YEAR), future.get(Calendar.MONTH), future.get(Calendar.DAY_OF_MONTH),
                        future.get(Calendar.HOUR_OF_DAY), future.get(Calendar.MINUTE),
                        future.get(Calendar.YEAR), future.get(Calendar.MONTH), future.get(Calendar.DAY_OF_MONTH),
                        future.get(Calendar.HOUR_OF_DAY), future.get(Calendar.MINUTE),
                        Instant.now(), Instant.now(),
                        0, 1, 0,
                        0
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

            // 3. Trigger broadcast to simulate alarm firing post-reschedule
            Intent alarmIntent = new Intent(context, AlarmReceiverService.class);
            alarmIntent.putExtra("id", String.valueOf(eventId));
            alarmIntent.putExtra("title", "Persistence Test");
            alarmIntent.putExtra("node", "Test Node");
            alarmIntent.putExtra("priority", 0);
            context.sendBroadcast(alarmIntent);

            Thread.sleep(2000);

            // 4. Open notification shade and check for the notification
            device.openNotification();

            // Wait for the notification to appear
            boolean found = device.wait(Until.hasObject(By.text("Persistence Test")), 5000);
            if (!found && device.hasObject(By.textContains("Active alarms"))) {
                device.findObject(By.textContains("Active alarms")).click();
                found = device.wait(Until.hasObject(By.text("Persistence Test")), 3000);
            }

            // Cleanup: close shade and delete event
            device.pressBack();
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                eventService.deleteEventOnOneRow(String.valueOf(eventId));
            });

            assertTrue("Notification not found in shade after reschedule", found);
        }
    }

}
