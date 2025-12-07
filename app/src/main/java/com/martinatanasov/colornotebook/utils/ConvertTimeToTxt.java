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

public class ConvertTimeToTxt {

    public String intToTxtTime(int h, int m) {
        String s = "";
        if (h < 10) {
            s += "0" + h;
        } else {
            s += h;
        }
        if (m < 10) {
            s += ":0" + m;
        } else {
            s += ":" + m;
        }
        return s;
    }
}
