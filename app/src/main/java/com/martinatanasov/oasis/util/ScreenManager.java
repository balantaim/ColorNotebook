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

package com.martinatanasov.oasis.util;

import android.content.res.Configuration;
import android.view.View;
import android.view.Window;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.martinatanasov.oasis.R;

public class ScreenManager {

    public ScreenManager(ComponentActivity activity, View content) {
        if (activity != null) {
            EdgeToEdge.enable(activity);
            configureSystemBarIcons(activity);
        }
        if (content != null) {
            applyInsets(content);
        }
    }

    @Deprecated
    public ScreenManager(View content, Window window, final boolean decorFitsSystemWindows) {
        if (content != null) {
            applyInsets(content);
        }
    }

    private void configureSystemBarIcons(ComponentActivity activity) {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(
                activity.getWindow(),
                activity.getWindow().getDecorView()
        );
        // White/light status bar icons for high contrast against dark toolbar backgrounds
        controller.setAppearanceLightStatusBars(false);

        // Determine if dark theme is active for navigation bar icons
        int nightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = (nightMode == Configuration.UI_MODE_NIGHT_YES);

        // Dark navigation icons on light theme, white navigation icons on dark theme
        controller.setAppearanceLightNavigationBars(!isDarkTheme);
    }

    private void applyInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemInsets = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
            );

            View toolbar = findToolbar(v);
            if (toolbar != null) {
                toolbar.setPadding(
                        systemInsets.left,
                        systemInsets.top,
                        systemInsets.right,
                        toolbar.getPaddingBottom()
                );

                View mainContainer = v.findViewById(R.id.mainContainer);
                View containerToPad = (mainContainer != null) ? mainContainer : v;

                if (containerToPad != v) {
                    containerToPad.setPadding(
                            systemInsets.left,
                            0,
                            systemInsets.right,
                            systemInsets.bottom
                    );
                    v.setPadding(0, 0, 0, 0);
                } else {
                    v.setPadding(
                            systemInsets.left,
                            0,
                            systemInsets.right,
                            systemInsets.bottom
                    );
                }
            } else {
                v.setPadding(
                        systemInsets.left,
                        systemInsets.top,
                        systemInsets.right,
                        systemInsets.bottom
                );
            }
            return insets;
        });
    }

    private View findToolbar(View root) {
        int[] toolbarIds = new int[]{
                R.id.toolbar,
                R.id.toolbar_add,
                R.id.toolbar_update,
                R.id.toolbar_option,
                R.id.toolbar_chart,
                R.id.toolbar_search_map
        };
        for (int id : toolbarIds) {
            View t = root.findViewById(id);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

}
