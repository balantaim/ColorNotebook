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

package com.martinatanasov.oasis.views.option;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AppLanguageTest {

    @Test
    public void testFromTag() {
        assertEquals(AppLanguage.SYSTEM_DEFAULT, AppLanguage.fromTag(null));
        assertEquals(AppLanguage.SYSTEM_DEFAULT, AppLanguage.fromTag(""));
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromTag("en"));
        assertEquals(AppLanguage.BULGARIAN, AppLanguage.fromTag("bg"));
        assertEquals(AppLanguage.RUSSIAN, AppLanguage.fromTag("ru"));
        assertEquals(AppLanguage.SYSTEM_DEFAULT, AppLanguage.fromTag("fr"));
    }

    @Test
    public void testGetTag() {
        assertEquals("", AppLanguage.SYSTEM_DEFAULT.getTag());
        assertEquals("en", AppLanguage.ENGLISH.getTag());
        assertEquals("bg", AppLanguage.BULGARIAN.getTag());
        assertEquals("ru", AppLanguage.RUSSIAN.getTag());
    }

}
