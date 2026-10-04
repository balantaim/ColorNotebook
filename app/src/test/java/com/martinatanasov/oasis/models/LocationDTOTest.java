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

package com.martinatanasov.oasis.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LocationDTOTest {

    @Test
    public void testLocationDTO() {
        LocationDTO location = new LocationDTO(42.6977, 23.3219, "Sofia, Bulgaria");
        assertEquals(42.6977, location.lat(), 0.0001);
        assertEquals(23.3219, location.lon(), 0.0001);
        assertEquals("Sofia, Bulgaria", location.locationName());
    }

}
