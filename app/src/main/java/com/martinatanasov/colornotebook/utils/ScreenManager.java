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

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

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
                    // Add padding at the left for navigation bar
                    systemInsets.left,
                    v.getPaddingTop(),
                    // Add padding at the right for navigation bar
                    systemInsets.right,
                    // Add padding at the bottom for navigation bar
                    systemInsets.bottom
            );
            return insets;
        });
    }

    private void setApplicationFullscreenMode(Window window, final boolean decorFitsSystemWindows) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, decorFitsSystemWindows);
            Objects.requireNonNull(window.getInsetsController()).hide(WindowInsets.Type.statusBars());
        } else {
            setApplicationFullscreenModeOnOlderSDK(window);
        }
    }

    @SuppressWarnings("deprecation")
    private void setApplicationFullscreenModeOnOlderSDK(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
