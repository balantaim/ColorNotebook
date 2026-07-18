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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.martinatanasov.colornotebook.services.AlarmReceiver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AlarmFunctionalityTest {

    private Context context;
    private UiDevice uiDevice;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Grant notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + context.getPackageName() + " android.permission.POST_NOTIFICATIONS");
        }
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

        // Wait for notification to appear
        uiDevice.openNotification();
        uiDevice.wait(Until.hasObject(By.text("Test Alarm")), 5000);

        // Verify notification content
        assertTrue("Notification title not found", uiDevice.hasObject(By.text("Test Alarm")));
        assertTrue("Notification text not found", uiDevice.hasObject(By.text("This is a test alarm message")));

        // Click on notification
        uiDevice.findObject(By.text("Test Alarm")).click();

        // Wait for CustomActivity to open
        uiDevice.wait(Until.hasObject(By.text("Test Alarm")), 5000);

        // Verify CustomActivity is displayed with the stop button
        assertNotNull("Stop button not found", uiDevice.findObject(By.res("com.martinatanasov.colornotebook:id/cancelAlarm")));
    }

}
