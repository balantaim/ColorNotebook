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

public class OrderFilterTest {

    @Test
    public void testOrderFilterResIds() {
        assertEquals(R.string.filter_a_z, OrderFilter.A_Z.getDisplayNameResId());
        assertEquals(R.string.filter_z_a, OrderFilter.Z_A.getDisplayNameResId());
        assertEquals(R.string.filter_date, OrderFilter.DATE.getDisplayNameResId());
        assertEquals(R.string.filter_reverse_date, OrderFilter.REVERSE_DATE.getDisplayNameResId());
    }

}
