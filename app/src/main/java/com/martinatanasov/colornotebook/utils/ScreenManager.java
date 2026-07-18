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

package com.martinatanasov.colornotebook.utils;

import android.view.View;
import android.view.Window;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class ScreenManager {

    public ScreenManager(View content, Window window, final boolean decorFitsSystemWindows) {
        // Make application to be on full screen
        setApplicationFullscreenMode(window, decorFitsSystemWindows);
        // Apply padding to avoid navigation bar overlap
        setPaddingToRootLayout(content);
    }

    private void setPaddingToRootLayout(View content) {
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemInsets.left,
                    systemInsets.top,
                    systemInsets.right,
                    systemInsets.bottom
            );
            return insets;
        });
    }

    private void setApplicationFullscreenMode(Window window, final boolean decorFitsSystemWindows) {
        WindowCompat.setDecorFitsSystemWindows(window, decorFitsSystemWindows);
        WindowInsetsControllerCompat windowInsetsControllerCompat = WindowCompat.getInsetsController(window, window.getDecorView());
        if (windowInsetsControllerCompat != null) {
            windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

}
