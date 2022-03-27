package com.martinatanasov.colornotebook;


import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TutorialActivityTest {

    @Rule
    public ActivityTestRule<TutorialActivity> mActivityTestRule = new ActivityTestRule<>(TutorialActivity.class);

    @Test
    public void tutorialActivityTest() {
    }
}
