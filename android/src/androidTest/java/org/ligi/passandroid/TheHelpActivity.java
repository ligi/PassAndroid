package org.ligi.passandroid;

import android.support.test.filters.SmallTest;
import com.squareup.spoon.Spoon;
import org.ligi.passandroid.ui.HelpActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static org.ligi.passandroid.steps.HelpSteps.checkThatHelpIsThere;


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

        checkThatHelpIsThere();

        Spoon.screenshot(getActivity(), "help");
    }

    @SmallTest
    public void test_that_help_finishes_on_home() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        assertTrue(getActivity().isFinishing());
    }
}
