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

package com.martinatanasov.oasis.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ConvertTimeToTxtTest {

    private ConvertTimeToTxt convertTimeToTxt;

    @Before
    public void setUp() {
        convertTimeToTxt = new ConvertTimeToTxt();
    }

    @Test
    public void testIntToTxtTimeSingleDigits() {
        assertEquals("05:08", convertTimeToTxt.intToTxtTime(5, 8));
    }

    @Test
    public void testIntToTxtTimeDoubleDigits() {
        assertEquals("14:35", convertTimeToTxt.intToTxtTime(14, 35));
    }

    @Test
    public void testIntToTxtTimeZero() {
        assertEquals("00:00", convertTimeToTxt.intToTxtTime(0, 0));
    }

    @Test
    public void testIntToTxtTimeMaxTime() {
        assertEquals("23:59", convertTimeToTxt.intToTxtTime(23, 59));
    }

}
