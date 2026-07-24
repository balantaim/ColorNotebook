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

package com.martinatanasov.colornotebook.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChartViewModel extends ViewModel {

    private final MutableLiveData<Float> _importantPercent = new MutableLiveData<>(0.0f);
    private final MutableLiveData<Float> _regularPercent = new MutableLiveData<>(0.0f);
    private final MutableLiveData<Float> _unimportantPercent = new MutableLiveData<>(0.0f);
    public LiveData<Float> importantPercent = _importantPercent;
    public LiveData<Float> regularPercent = _regularPercent;
    public LiveData<Float> unimportantPercent = _unimportantPercent;

    public void calculatePieChartData(String important, String regular, String unimportant) {
        try {
            int impCount = Integer.parseInt(important);
            int regCount = Integer.parseInt(regular);
            int uniCount = Integer.parseInt(unimportant);
            int total = impCount + regCount + uniCount;

            if (total > 0) {
                float percentPerPart = 100f / total;
                _importantPercent.setValue(percentPerPart * impCount);
                _regularPercent.setValue(percentPerPart * regCount);
                _unimportantPercent.setValue(percentPerPart * uniCount);
            } else {
                _importantPercent.setValue(0f);
                _regularPercent.setValue(0f);
                _unimportantPercent.setValue(0f);
            }
        } catch (NumberFormatException e) {
            _importantPercent.setValue(0f);
            _regularPercent.setValue(0f);
            _unimportantPercent.setValue(0f);
        }
    }

}
