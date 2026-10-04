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

package com.martinatanasov.oasis.views.main;

import static org.junit.Assert.assertEquals;

import com.martinatanasov.oasis.R;

import org.junit.Test;

public class PriorityFilterTest {

    @Test
    public void testPriorityFilterResIds() {
        assertEquals(R.string.filter_no_preference, PriorityFilter.NONE.getDisplayNameResId());
        assertEquals(R.string.drawer_one_priority_important, PriorityFilter.IMPORTANT.getDisplayNameResId());
        assertEquals(R.string.drawer_two_priority_regular, PriorityFilter.REGULAR.getDisplayNameResId());
        assertEquals(R.string.drawer_three_priority_unimportant, PriorityFilter.UNIMPORTANT.getDisplayNameResId());
    }

}
