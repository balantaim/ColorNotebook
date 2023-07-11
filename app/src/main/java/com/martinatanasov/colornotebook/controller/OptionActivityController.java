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

package com.martinatanasov.colornotebook.controller;

import com.martinatanasov.colornotebook.tools.PreferencesManager;
import com.martinatanasov.colornotebook.view.option.OptionActivity;

public class OptionActivityController {
    private OptionActivity optionView;
    private final PreferencesManager preferencesManager;

    public OptionActivityController(OptionActivity optionView){
        this.optionView = optionView;
        preferencesManager = new PreferencesManager(this.optionView);
    }
    public int getTheme(){
        return preferencesManager.getCurrentTheme();
    }
    public boolean getForceDarkValue(){
        return preferencesManager.getForceDarkMode();
    }
    public void setTheme(int value){
        preferencesManager.setThemeOnDisc(value);
    }
    public void setForceDarkValue(boolean value){
        preferencesManager.setForceDarkOnDisc(value);
    }

}
