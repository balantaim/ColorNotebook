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

package com.martinatanasov.oasis.services;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.martinatanasov.oasis.models.LocationDTO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class MapServiceImplTest {

    private MapServiceImpl mapService;

    @Before
    public void setUp() {
        mapService = new MapServiceImpl("ColorNotebookTest");
    }

    @Test
    public void testSearchNullQuery() {
        assertNull(mapService.search(null));
        assertNull(mapService.search(""));
        assertNull(mapService.search("   "));
    }

    @Test
    public void testSuggestNullQuery() {
        List<LocationDTO> resultNull = mapService.suggest(null, 5);
        assertTrue(resultNull.isEmpty());

        List<LocationDTO> resultEmpty = mapService.suggest("  ", 5);
        assertTrue(resultEmpty.isEmpty());
    }

}
