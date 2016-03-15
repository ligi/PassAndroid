package org.ligi.passandroid.steps;

import org.ligi.passandroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class HelpSteps {

    public static void checkThatHelpIsThere() {
        onView(withId(R.id.help_text)).check(matches(isDisplayed()));
    }


}
