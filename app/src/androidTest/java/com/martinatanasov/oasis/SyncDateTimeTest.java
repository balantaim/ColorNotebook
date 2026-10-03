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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.martinatanasov.oasis.services.EventService;
import com.martinatanasov.oasis.services.EventServiceImpl;
import com.martinatanasov.oasis.views.main.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SyncDateTimeTest {

    public static Matcher<View> withSameTextAs(final int targetViewId) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with same text as view with id: " + targetViewId);
            }

            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof TextView)) {
                    return false;
                }
                View targetView = view.getRootView().findViewById(targetViewId);
                if (!(targetView instanceof TextView)) {
                    return false;
                }
                return ((TextView) view).getText().toString().equals(((TextView) targetView).getText().toString());
            }
        };
    }

    @Before
    public void setUp() {
        // Disable tutorial and clear database before launching activity
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("disableTutorial", true).commit();
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            try (EventService eventService = new EventServiceImpl(context)) {
                eventService.deleteAllEvents();
            }
        });
    }

    @Test
    public void testSyncFutureDateUpdatesEndDate() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.add_button)).perform(click());
            onView(withId(R.id.advOptions)).perform(scrollTo(), click());

            Calendar futureDate = Calendar.getInstance();
            futureDate.add(Calendar.DAY_OF_MONTH, 2);
            int year = futureDate.get(Calendar.YEAR);
            int month = futureDate.get(Calendar.MONTH) + 1;
            int day = futureDate.get(Calendar.DAY_OF_MONTH);

            onView(withId(R.id.startDate)).perform(scrollTo(), click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(year, month, day));
            onView(withId(android.R.id.button1)).perform(click());

            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            String expectedDate = sdf.format(futureDate.getTime());

            onView(withId(R.id.startDate)).check(matches(withText(expectedDate)));
            onView(withId(R.id.endDate)).check(matches(withText(expectedDate)));
        }
    }

    @Test
    public void testSyncFutureTimeUpdatesEndTime() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.add_button)).perform(click());
            onView(withId(R.id.advOptions)).perform(scrollTo(), click());

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            onView(withId(R.id.startDate)).perform(scrollTo(), click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(
                    tomorrow.get(Calendar.YEAR),
                    tomorrow.get(Calendar.MONTH) + 1,
                    tomorrow.get(Calendar.DAY_OF_MONTH)));
            onView(withId(android.R.id.button1)).perform(click());

            onView(withId(R.id.startTime)).perform(scrollTo(), click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(20, 0));
            onView(withId(android.R.id.button1)).perform(click());

            onView(withId(R.id.endTime)).check(matches(withSameTextAs(R.id.startTime)));
        }
    }

    @Test
    public void testSyncOnUpdateActivity() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.add_button)).perform(click());
            onView(withId(R.id.eventTitle)).perform(typeText("Test Event"), closeSoftKeyboard());
            onView(withId(R.id.btnAdd)).perform(click());

            onView(withId(R.id.recyclerView)).perform(androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.advOptions2)).perform(scrollTo(), click());

            Calendar toDate = Calendar.getInstance();
            toDate.add(Calendar.DAY_OF_MONTH, 2);
            onView(withId(R.id.endDate2)).perform(scrollTo(), click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(
                    toDate.get(Calendar.YEAR),
                    toDate.get(Calendar.MONTH) + 1,
                    toDate.get(Calendar.DAY_OF_MONTH)));
            onView(withId(android.R.id.button1)).perform(click());

            onView(withId(R.id.endTime2)).perform(scrollTo(), click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(10, 0));
            onView(withId(android.R.id.button1)).perform(click());

            Calendar fromDate = Calendar.getInstance();
            fromDate.add(Calendar.DAY_OF_MONTH, 3);
            onView(withId(R.id.startDate2)).perform(scrollTo(), click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(
                    fromDate.get(Calendar.YEAR),
                    fromDate.get(Calendar.MONTH) + 1,
                    fromDate.get(Calendar.DAY_OF_MONTH)));
            onView(withId(android.R.id.button1)).perform(click());

            onView(withId(R.id.endDate2)).check(matches(withSameTextAs(R.id.startDate2)));
            onView(withId(R.id.endTime2)).check(matches(withSameTextAs(R.id.startTime2)));

            onView(withId(R.id.startTime2)).perform(scrollTo(), click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(23, 0));
            onView(withId(android.R.id.button1)).perform(click());

            onView(withId(R.id.endTime2)).check(matches(withSameTextAs(R.id.startTime2)));
        }
    }

    @Test
    public void testEndTimeConstraint() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.add_button)).perform(click());
            onView(withId(R.id.advOptions)).perform(scrollTo(), click());

            onView(withId(R.id.startTime)).perform(scrollTo(), click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(15, 0));
            onView(withId(android.R.id.button1)).perform(click());

            onView(withId(R.id.endTime)).perform(scrollTo(), click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(14, 0));
            onView(withId(android.R.id.button1)).perform(click());

            onView(withId(R.id.endTime)).check(matches(withSameTextAs(R.id.startTime)));
        }
    }

}
