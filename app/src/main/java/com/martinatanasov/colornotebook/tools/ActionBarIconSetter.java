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

package com.martinatanasov.colornotebook.tools;

import androidx.appcompat.app.ActionBar;

import com.martinatanasov.colornotebook.R;

public class ActionBarIconSetter {
    public void setArrowBackIcon(ActionBar supportActionBar) {
        assert supportActionBar != null;
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_custom_arrow);
    }
}
