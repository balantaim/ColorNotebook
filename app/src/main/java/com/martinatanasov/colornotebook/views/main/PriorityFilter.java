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

public enum PriorityFilter {
    NONE(R.string.filter_no_preference),
    IMPORTANT(R.string.drawer_one_priority_important),
    REGULAR(R.string.drawer_two_priority_regular),
    UNIMPORTANT(R.string.drawer_three_priority_unimportant);
    private final int displayNameResId;

    PriorityFilter(int displayNameResId) {
        this.displayNameResId = displayNameResId;
    }

    public int getDisplayNameResId() {
        return displayNameResId;
    }
}
