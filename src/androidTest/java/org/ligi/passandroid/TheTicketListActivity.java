package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.ui.TicketListActivity;

import java.util.ArrayList;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

@TargetApi(14)
public class TheTicketListActivity extends BaseIntegration<TicketListActivity> {

    public TheTicketListActivity() {
        super(TicketListActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final ArrayList<Pass> list = new ArrayList<Pass>() {{
            add(new PassImpl());
        }};

        App.replacePassStore(new FixedPassListPassStore(list));

        getActivity();
    }

    @MediumTest
    public void test_that_list_is_there() {

        onView(withId(R.id.content_list)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "list");
    }

    @MediumTest
    public void test_help_goes_to_help() {
        onView(withId(R.id.menu_help)).perform(click());
        onView(withId(R.id.help_tv)).check(matches(isDisplayed()));
    }

    @MediumTest
    public void test_that_navigation_drawer_usualy_not_shown() {
        onView(withId(R.id.left_drawer)).check(matches(not(isDisplayed())));
    }

    @MediumTest

    public void test_that_navigation_drawer_opens() {
        //onView(withId(android.R.id.home)).perform(click());
        //onView(withId(R.id.left_drawer)).check(matches(isDisplayed()));

        Spoon.screenshot(getActivity(), "open_drawer");
    }
}
