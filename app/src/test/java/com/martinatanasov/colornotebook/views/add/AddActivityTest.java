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

package com.martinatanasov.colornotebook.views.add;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.DatePickerDialog;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;

import com.martinatanasov.colornotebook.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowDialog;

@RunWith(RobolectricTestRunner.class)
public class AddActivityTest {

    @Test
    public void testDatePickerPersistenceOnRotation() {
        try (ActivityScenario<AddActivity> scenario = ActivityScenario.launch(AddActivity.class)) {
            scenario.onActivity(activity -> {
                TextView startDate = activity.findViewById(R.id.startDate);
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
