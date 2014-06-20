package org.ligi.passandroid;

import android.test.suitebuilder.annotation.SmallTest;

import com.squareup.spoon.Spoon;

import org.ligi.passandroid.ui.HelpActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;


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
    public void test_that_help_is_there() {
        onView(withId(R.id.help_tv)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "help");
    }


    @SmallTest
    public void test_that_help_finishes_on_home() {
        //onView(withId(android.R.id.home)).perform(click());
    }
}
