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

package com.martinatanasov.colornotebook.views.update;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.martinatanasov.colornotebook.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowDialog;

@RunWith(RobolectricTestRunner.class)
public class UpdateActivityTest {

    @Test
    public void testDatePickerPersistenceOnRotation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UpdateActivity.class);
        intent.putExtra("id", "1");
        intent.putExtra("title", "Test Event");

        try (ActivityScenario<UpdateActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                TextView startDate = activity.findViewById(R.id.startDate2);
                startDate.performClick();
            });

            // Verify dialog is showing
            assertTrue(ShadowDialog.getLatestDialog() instanceof DatePickerDialog);
            assertNotNull(ShadowDialog.getLatestDialog());
            assertTrue(ShadowDialog.getLatestDialog().isShowing());

            // Rotate activity
            scenario.recreate();

            // Verify dialog is still showing after recreation
            scenario.onActivity(activity -> {
                assertTrue(ShadowDialog.getLatestDialog() instanceof DatePickerDialog);
                assertTrue(ShadowDialog.getLatestDialog().isShowing());
            });
        }
    }

}
