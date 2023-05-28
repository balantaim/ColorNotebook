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

package com.martinatanasov.colornotebook.view.tutorial;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.martinatanasov.colornotebook.R;

public class TutorialActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    IntroAdapter introAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set Theme NoActionBar default colors
        //setTheme(R.style.Theme_Tutorial);
        setContentView(R.layout.activity_tutorial);

        viewPager2=findViewById(R.id.viewPager);

        FragmentManager fm = getSupportFragmentManager();
        introAdapter = new IntroAdapter(fm, getLifecycle());
        //set viewPager2 animation for the transition
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());
        viewPager2.setAdapter(introAdapter);
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}