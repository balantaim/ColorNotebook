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

import android.app.NotificationManager;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class NotificationCreatorTest {

    @Test
    public void testCreateNotificationChannel() {
        Context context = ApplicationProvider.getApplicationContext();
        NotificationCreator notificationCreator = new NotificationCreator();
        notificationCreator.createNotificationChannel(context);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            org.junit.Assert.assertNotNull(notificationManager.getNotificationChannel("sound_notifications_channel"));
            org.junit.Assert.assertNotNull(notificationManager.getNotificationChannel("silent_notifications_channel"));
        }
    }

}
