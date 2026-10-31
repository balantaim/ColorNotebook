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

package com.martinatanasov.oasis.util;

import static org.junit.Assert.assertEquals;

import com.martinatanasov.oasis.R;

import org.junit.Test;

public class ColorUtilTest {

    @Test
    public void testGetColorCount() {
        assertEquals(30, ColorUtil.getColorCount());
    }

    @Test
    public void testGetEventColorRes_validIndices() {
        assertEquals(R.color.error, ColorUtil.getEventColorRes(1));
        assertEquals(R.color.pick_maroon, ColorUtil.getEventColorRes(2));
        assertEquals(R.color.pick_rose, ColorUtil.getEventColorRes(3));
        assertEquals(R.color.pick_pink, ColorUtil.getEventColorRes(4));
        assertEquals(R.color.pick_deep_orange, ColorUtil.getEventColorRes(5));
        assertEquals(R.color.pick_orange, ColorUtil.getEventColorRes(6));
        assertEquals(R.color.pick_peach, ColorUtil.getEventColorRes(7));
        assertEquals(R.color.pick_amber, ColorUtil.getEventColorRes(8));
        assertEquals(R.color.pick_yellow, ColorUtil.getEventColorRes(9));
        assertEquals(R.color.pick_lime, ColorUtil.getEventColorRes(10));
        assertEquals(R.color.pick_light_green, ColorUtil.getEventColorRes(11));
        assertEquals(R.color.pick_green, ColorUtil.getEventColorRes(12));
        assertEquals(R.color.pick_mint, ColorUtil.getEventColorRes(13));
        assertEquals(R.color.pick_olive, ColorUtil.getEventColorRes(14));
        assertEquals(R.color.pick_teal, ColorUtil.getEventColorRes(15));
        assertEquals(R.color.pick_cyan, ColorUtil.getEventColorRes(16));
        assertEquals(R.color.pick_sky_blue, ColorUtil.getEventColorRes(17));
        assertEquals(R.color.pick_blue, ColorUtil.getEventColorRes(18));
        assertEquals(R.color.pick_navy, ColorUtil.getEventColorRes(19));
        assertEquals(R.color.pick_indigo, ColorUtil.getEventColorRes(20));
        assertEquals(R.color.pick_deep_purple, ColorUtil.getEventColorRes(21));
        assertEquals(R.color.pick_purple, ColorUtil.getEventColorRes(22));
        assertEquals(R.color.pick_lavender, ColorUtil.getEventColorRes(23));
        assertEquals(R.color.pick_plum, ColorUtil.getEventColorRes(24));
        assertEquals(R.color.pick_magenta, ColorUtil.getEventColorRes(25));
        assertEquals(R.color.brown, ColorUtil.getEventColorRes(26));
        assertEquals(R.color.pick_bronze, ColorUtil.getEventColorRes(27));
        assertEquals(R.color.gray_new, ColorUtil.getEventColorRes(28));
        assertEquals(R.color.pick_charcoal, ColorUtil.getEventColorRes(29));
    }

    @Test
    public void testGetEventColorRes_fallback() {
        assertEquals(R.color.gray_new, ColorUtil.getEventColorRes(-1));
        assertEquals(R.color.gray_new, ColorUtil.getEventColorRes(100));
    }

}
