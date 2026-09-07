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

package com.martinatanasov.colornotebook.views.main;

import com.martinatanasov.colornotebook.R;

public enum OrderFilter {
    A_Z(R.string.filter_a_z),
    Z_A(R.string.filter_z_a),
    DATE(R.string.filter_date),
    REVERSE_DATE(R.string.filter_reverse_date);
    private final int displayNameResId;

    OrderFilter(int displayNameResId) {
        this.displayNameResId = displayNameResId;
    }

    public int getDisplayNameResId() {
        return displayNameResId;
    }
}
