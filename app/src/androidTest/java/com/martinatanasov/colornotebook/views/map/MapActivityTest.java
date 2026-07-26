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

package com.martinatanasov.colornotebook.views.map;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.martinatanasov.colornotebook.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {

    @Test
    public void testLocationConfirmationFlow() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, MapActivity.class);
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launchActivityForResult(intent)) {
            // 1. Verify confirmButton is initially disabled
            onView(withId(R.id.confirmButton)).check(matches(not(isEnabled())));

            // 2. Enter "Varna" and click "Search"
            onView(withId(R.id.searchEditText)).perform(typeText("Plovdiv"));
            onView(withId(R.id.searchButton)).perform(click());

            // Wait for search result (MapService runs on a new thread)
            waitFor(4000);

            // 3. Verify confirmButton becomes enabled
            onView(withId(R.id.confirmButton)).check(matches(isEnabled()));

            // 4. Click the "X" button (Clear text icon in TextInputLayout)
            // Material components default content description for clear text is "Clear text"
            onView(withContentDescription("Clear text")).perform(click());

            // 5. Verify confirmButton becomes disabled
            onView(withId(R.id.confirmButton)).check(matches(not(isEnabled())));

            // 6. Enter "Sofia" and click "Search"
            onView(withId(R.id.searchEditText)).perform(typeText("Sofia"));
            onView(withId(R.id.searchButton)).perform(click());

            waitFor(4000);

            // 7. Verify confirmButton becomes enabled
            onView(withId(R.id.confirmButton)).check(matches(isEnabled()));

            // 8. Click confirm and verify result
            onView(withId(R.id.confirmButton)).perform(click());

            Instrumentation.ActivityResult result = scenario.getResult();
            assertEquals(Activity.RESULT_OK, result.getResultCode());
            assertNotNull(result.getResultData());
            String location = result.getResultData().getStringExtra("location");
            assertNotNull(location);
            String lowerLocation = location.toLowerCase();
            assertTrue("Location should contain Sofia or София, but was: " + location,
                    lowerLocation.contains("sofia") || lowerLocation.contains("софия"));
        }
    }

    private void waitFor(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
