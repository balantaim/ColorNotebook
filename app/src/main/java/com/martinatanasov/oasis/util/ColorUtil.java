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

import com.martinatanasov.oasis.R;

public final class ColorUtil {

    private ColorUtil() {
    }

    public static int getEventColorRes(int colorIndex) {
        return switch (colorIndex) {
            case 1 -> R.color.error;
            case 2 -> R.color.pick_maroon;
            case 3 -> R.color.pick_rose;
            case 4 -> R.color.pick_pink;
            case 5 -> R.color.pick_deep_orange;
            case 6 -> R.color.pick_orange;
            case 7 -> R.color.pick_peach;
            case 8 -> R.color.pick_amber;
            case 9 -> R.color.pick_yellow;
            case 10 -> R.color.pick_lime;
            case 11 -> R.color.pick_light_green;
            case 12 -> R.color.pick_green;
            case 13 -> R.color.pick_mint;
            case 14 -> R.color.pick_olive;
            case 15 -> R.color.pick_teal;
            case 16 -> R.color.pick_cyan;
            case 17 -> R.color.pick_sky_blue;
            case 18 -> R.color.pick_blue;
            case 19 -> R.color.pick_navy;
            case 20 -> R.color.pick_indigo;
            case 21 -> R.color.pick_deep_purple;
            case 22 -> R.color.pick_purple;
            case 23 -> R.color.pick_lavender;
            case 24 -> R.color.pick_plum;
            case 25 -> R.color.pick_magenta;
            case 26 -> R.color.brown;
            case 27 -> R.color.pick_bronze;
            case 28 -> R.color.gray_new;
            case 29 -> R.color.pick_charcoal;
            default -> R.color.gray_new;
        };
    }

    public static int getColorCount() {
        return 30;
    }

}
