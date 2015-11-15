package org.ligi.passandroid;

import android.test.suitebuilder.annotation.SmallTest;

import com.squareup.spoon.Spoon;

import org.ligi.passandroid.ui.HelpActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.allOf;


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

    @SmallTest
    public void test_that_help_finishes_on_home() {
        onView(allOf(withContentDescription(containsString("Navigate up")), isClickable())).perform(click());
        assertTrue(getActivity().isFinishing());
    }
}
