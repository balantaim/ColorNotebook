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

package com.martinatanasov.colornotebook.utils.events;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

@SuppressWarnings("deprecation")
public class VibrationUtil {

    private static final long VIBRATION_DURATION = 200;
    private final Vibrator vibrator;

    public VibrationUtil(Activity activity) {
        // 1. Use VibratorManager for Android 12 (API 31) and newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) activity.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            // Backward compatibility for older versions
            vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

//    public void startEffect(Activity activity) {
//        Vibrator vibrator = (Vibrator) activity.getSystemService(VIBRATOR_SERVICE);
//        //Check if the device has vibrator hardware
//        if (vibrator.hasVibrator()) {
//            //Check if the Android version is 8 or newer
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//

    /// /                long[] timings = new long[] { 50, 50, 50, 50, 50, 100, 350, 250 };
    /// /                int[] amplitudes = new int[] { 77, 79, 84, 99, 143, 255, 0, 255 };
    /// /                int repeatIndex = -1; // Do not repeat.
    /// /                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex));
//                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
//            }
//        }
//    }
    public void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect effect = VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE);

                // Use VibrationAttributes for Android 13 (API 33) and newer
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    vibrator.vibrate(effect, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_NOTIFICATION));
                } else {
                    vibrator.vibrate(effect);
                }
            } else {
                // Fallback for very old devices (Pre-Oreo)
                vibrator.vibrate(VIBRATION_DURATION);
            }
        }
    }

}
