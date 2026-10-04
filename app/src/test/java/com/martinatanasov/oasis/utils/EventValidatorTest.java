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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class EventValidatorTest {

    private EventValidator validator;

    @Before
    public void setUp() {
        validator = new EventValidator() {
        };
    }

    @Test
    public void testIsEventTitleValidWithNull() {
        assertFalse(validator.isEventTitleValid(null));
    }

    @Test
    public void testIsEventTitleValidWithEmpty() {
        assertFalse(validator.isEventTitleValid(""));
    }

    @Test
    public void testIsEventTitleValidWithOneChar() {
        assertFalse(validator.isEventTitleValid("A"));
    }

    @Test
    public void testIsEventTitleValidWithTwoChars() {
        assertTrue(validator.isEventTitleValid("AB"));
    }

    @Test
    public void testIsEventTitleValidWithLongString() {
        assertTrue(validator.isEventTitleValid("Meeting with Team"));
    }

}
