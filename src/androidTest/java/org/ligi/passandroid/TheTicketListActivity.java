package org.ligi.passandroid;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.passandroid.ui.TicketListActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;

public class TheTicketListActivity extends ActivityInstrumentationTestCase2<TicketListActivity> {

    public TheTicketListActivity() {
        super(TicketListActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void test_that_list_is_there() {
        Spoon.screenshot(getActivity(), "list_activity");
        onView(withId(R.id.content_list)).check(matches(isDisplayed()));
    }

    @MediumTest
    public void test_help_goes_to_help() {
        onView(withId(R.id.menu_help)).perform(click());
        onView(withId(R.id.help_tv)).check(matches(isDisplayed()));
    }
}
