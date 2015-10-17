package org.ligi.passandroid;

import android.test.suitebuilder.annotation.SmallTest;

import com.squareup.spoon.Spoon;

import org.ligi.passandroid.ui.HelpActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class TheHelpActivity extends BaseIntegration<HelpActivity> {

    public TheHelpActivity() {
        super(HelpActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @SmallTest
    public void testHelpIsThere() {
        onView(withId(R.id.help_tv)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "help");
    }


    /*
    depends on this: http://stackoverflow.com/questions/23985181/click-home-icon-with-espresso
    @SmallTest
    public void test_that_help_finishes_on_home() {
        onView(withId(android.R.id.home)).perform(click());
    }
    */
}
