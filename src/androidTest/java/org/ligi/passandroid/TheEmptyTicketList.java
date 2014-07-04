package org.ligi.passandroid;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.ui.TicketListActivity;

import java.util.ArrayList;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class TheEmptyTicketList extends BaseIntegration<TicketListActivity> {

    public TheEmptyTicketList() {
        super(TicketListActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        App.replacePassStore(new FixedPassListPassStore(new ArrayList<Pass>()));
        getActivity();
    }

    @MediumTest
    public void test_that_empty_view_is_there_without_passes() {

        onView(withId(R.id.emptyView)).check(matches(isDisplayed()));

        Spoon.screenshot(getActivity(), "empty_view");
    }

    @MediumTest
    public void testHelpGoesToHelp() {
        onView(withId(R.id.menu_help)).perform(click());
        onView(withId(R.id.help_tv)).check(matches(isDisplayed()));
    }
}
