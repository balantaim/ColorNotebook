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

package com.martinatanasov.colornotebook.tools.events;

import static android.content.Context.VIBRATOR_SERVICE;

import android.app.Activity;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationEvent {

    public void startEffect(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(VIBRATOR_SERVICE);
        //Check if the device has vibrator hardware
        if(vibrator.hasVibrator()) {
            //Check if the Android version is 8 or newer
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//                long[] timings = new long[] { 50, 50, 50, 50, 50, 100, 350, 250 };
//                int[] amplitudes = new int[] { 77, 79, 84, 99, 143, 255, 0, 255 };
//                int repeatIndex = -1; // Do not repeat.
//                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex));
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }
}
