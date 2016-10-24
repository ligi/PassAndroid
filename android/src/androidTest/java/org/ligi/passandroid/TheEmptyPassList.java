package org.ligi.passandroid;

import com.squareup.spoon.Spoon;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.gobandroid_hd.base.PassandroidTestRule;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.ui.PassListActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.ligi.passandroid.steps.HelpSteps.checkThatHelpIsThere;


public class TheEmptyPassList {

    @Rule
    public PassandroidTestRule<PassListActivity> rule = new PassandroidTestRule<>(PassListActivity.class, false);

    @Before
    public void setUp() {
        App.setComponent(DaggerTestComponent.builder().testModule(new TestModule(new ArrayList<Pass>())).build());
        rule.launchActivity(null);
    }

    @Test
    public void testEmptyViewIsThereWhenThereAreNoPasses() {
        Spoon.screenshot(rule.getActivity(), "empty_view");
        onView(withId(R.id.emptyView)).check(matches(isDisplayed()));
    }

    @Test
    public void testHelpGoesToHelp() {
        onView(withId(R.id.menu_help)).perform(click());

        checkThatHelpIsThere();
    }

}
