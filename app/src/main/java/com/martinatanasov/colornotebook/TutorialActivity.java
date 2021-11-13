package com.martinatanasov.colornotebook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

public class TutorialActivity extends AppCompatActivity {


    ViewPager2 viewPager2;
    IntroAdapter introAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //set Theme NoActionBar default colors
        setTheme(R.style.Theme_Tutorial);
        setContentView(R.layout.activity_tutorial);

        viewPager2=findViewById(R.id.viewPager);

        FragmentManager fm = getSupportFragmentManager();
        introAdapter = new IntroAdapter(fm, getLifecycle());
        //set viewPager2 animation for the transition
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());
        viewPager2.setAdapter(introAdapter);
    }
}