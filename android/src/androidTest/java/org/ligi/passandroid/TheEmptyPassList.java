package org.ligi.passandroid;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.ui.PassListActivity;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class TheEmptyPassList extends BaseIntegration<PassListActivity> {

    public TheEmptyPassList() {
        super(PassListActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        App.setComponent(DaggerTestComponent.builder().testModule(new TestModule(new ArrayList<Pass>())).build());
        getActivity();
    }

    @MediumTest
    public void testEmptyViewIsThereWhenThereAreNoPasses() {
        Spoon.screenshot(getActivity(), "empty_view");
        onView(withId(R.id.emptyView)).check(matches(isDisplayed()));
    }

    @MediumTest
    public void testHelpGoesToHelp() {
        onView(withId(R.id.menu_help)).perform(click());
        onView(withId(R.id.help_tv)).check(matches(isDisplayed()));
    }

}
