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
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.martinatanasov.colornotebook.services.AlarmReceiver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AlarmFunctionalityTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    private Context context;
    private UiDevice uiDevice;

    @Before
    public void setUp() throws Exception {
        context = ApplicationProvider.getApplicationContext();
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.wakeUp();
        uiDevice.pressHome();
    }

    @Test
    public void testAlarmNotificationAppears() {
        // Prepare intent for AlarmReceiver
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", "9999");
        intent.putExtra("title", "Test Alarm");
        intent.putExtra("node", "This is a test alarm message");
        intent.putExtra("priority", 0);

        // Send broadcast to trigger AlarmReceiver
        context.sendBroadcast(intent);

        // Small delay to let broadcast be processed
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        // Wait for notification to appear
        uiDevice.openNotification();

        // Wait longer and use more flexible matching
        boolean foundSpecific = uiDevice.wait(Until.hasObject(By.textContains("Test Alarm")), 10000);
        boolean foundSummary = uiDevice.hasObject(By.textContains("Active alarms"));

        if (!foundSpecific && foundSummary) {
            // Group might be collapsed, try clicking the summary to expand
            uiDevice.findObject(By.textContains("Active alarms")).click();
            // Wait again for specific notification
            foundSpecific = uiDevice.wait(Until.hasObject(By.textContains("Test Alarm")), 5000);
        }

        // Verify notification content
        assertTrue("Notification not found. Specific: " + foundSpecific + ", Summary: " + foundSummary,
                foundSpecific || foundSummary);

        if (foundSpecific) {
            // Click on notification
            uiDevice.findObject(By.textContains("Test Alarm")).click();
        } else {
            // If only summary was found, click it
            uiDevice.findObject(By.textContains("Active alarms")).click();
        }

        // Wait for CustomActivity to open
        uiDevice.wait(Until.hasObject(By.textContains("Test Alarm")), 10000);

        // Verify CustomActivity is displayed with the stop button
        assertTrue("Stop button not found", uiDevice.wait(Until.hasObject(By.res("com.martinatanasov.colornotebook:id/cancelAlarm")), 5000));

        // Cleanup: Click the cancel button to stop the alarm
        uiDevice.findObject(By.res("com.martinatanasov.colornotebook:id/cancelAlarm")).click();
    }

}
