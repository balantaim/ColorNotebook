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

package com.martinatanasov.colornotebook.repositories;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.martinatanasov.colornotebook.views.main.OrderFilter;
import com.martinatanasov.colornotebook.views.main.PriorityFilter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PreferencesManagerTest {

    private PreferencesManager preferencesManager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        preferencesManager = new PreferencesManager(context);
    }

    @Test
    public void testOrderFilterPersistence() {
        // Initial value
        assertEquals(OrderFilter.DATE, preferencesManager.getOrderFilter());

        // Save new value
        preferencesManager.setOrderFilter(OrderFilter.Z_A);
        assertEquals(OrderFilter.Z_A, preferencesManager.getOrderFilter());

        // New instance should also have the value
        PreferencesManager newManager = new PreferencesManager(ApplicationProvider.getApplicationContext());
        assertEquals(OrderFilter.Z_A, newManager.getOrderFilter());
    }

    @Test
    public void testPriorityFilterPersistence() {
        // Initial value
        assertEquals(PriorityFilter.NONE, preferencesManager.getPriorityFilter());

        // Save new value
        preferencesManager.setPriorityFilter(PriorityFilter.IMPORTANT);
        assertEquals(PriorityFilter.IMPORTANT, preferencesManager.getPriorityFilter());

        // New instance should also have the value
        PreferencesManager newManager = new PreferencesManager(ApplicationProvider.getApplicationContext());
        assertEquals(PriorityFilter.IMPORTANT, newManager.getPriorityFilter());
    }

}
