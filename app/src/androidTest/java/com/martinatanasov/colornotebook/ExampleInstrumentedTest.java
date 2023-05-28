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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.martinatanasov.colornotebook.view.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {
    @Rule
    //Start MainActivity when the test is started
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    //This is UI test with Espresso
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.martinatanasov.colornotebook", appContext.getPackageName());
        //Check if the button of the navigation drawer is visible
        onView(withId(R.id.navigation_button)).check(matches(isDisplayed()));
        //Open the navigation drawer menu
        onView(withId(R.id.navigation_button)).perform(click());
        //Navigate to About Fragment
        onView(withId(R.id.about)).perform(click());
        //Check if the button is displayed and perform click
        onView(withId(R.id.checkDev)).check(matches(isDisplayed())).perform(click());
        //This is timeout for the POST query

        //SystemClock.sleep(3000);
        //This is Positive test and the internet connection should be disrupted!
        //Check if the error message is presented from string resource R.string.error_404
        onView(withId(R.id.txtDevelopers)).check(matches(withText(R.string.error_404)));
        //Perform click in order to return to the main screen
        onView(withId(R.id.txtDevelopers)).perform(click());

    }
}