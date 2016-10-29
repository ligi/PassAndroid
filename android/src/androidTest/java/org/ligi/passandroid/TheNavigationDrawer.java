package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import com.squareup.spoon.Spoon;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.gobandroid_hd.base.PassandroidTestRule;
import org.ligi.passandroid.ui.PassListActivity;
import org.ligi.passandroid.ui.PreferenceActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@TargetApi(14)
public class TheNavigationDrawer {

    @Rule
    public PassandroidTestRule<PassListActivity> rule = new PassandroidTestRule<>(PassListActivity.class, true);

    @Test
    public void testNavigationDrawerIsUsuallyNotShown() {
        onView(withId(R.id.navigationView)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testThatNavigationDrawerOpens() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.navigationView)).check(matches(isDisplayed()));
    }

    @Test
    public void testBetatestClick() {
        testThatNavigationDrawerOpens();
        Spoon.screenshot(rule.getActivity(), "open_drawer");

        intending(hasAction(Intent.ACTION_VIEW)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

        onView(withText(R.string.nav_betatest_opt_in_out)).perform(click());

        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData("https://play.google.com/apps/testing/org.ligi.passandroid")));
    }


    @Test
    public void testCommunityClick() {
        testThatNavigationDrawerOpens();
        Spoon.screenshot(rule.getActivity(), "open_drawer");

        intending(hasAction(Intent.ACTION_VIEW)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

        onView(withText(R.string.nav_community_on_google)).perform(click());

        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData("https://plus.google.com/communities/116353894782342292067")));
    }

    @Test
    public void testGitHubClick() {
        testThatNavigationDrawerOpens();
        Spoon.screenshot(rule.getActivity(), "open_drawer");

        intending(hasAction(Intent.ACTION_VIEW)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

        onView(withText(R.string.nav_github)).perform(click());

        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData("https://github.com/ligi/PassAndroid")));
    }

    @Test
    public void testImproveTranslationsClick() {
        testThatNavigationDrawerOpens();
        Spoon.screenshot(rule.getActivity(), "open_drawer");

        intending(hasAction(Intent.ACTION_VIEW)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

        onView(withText(R.string.nav_improve_translation)).perform(click());

        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData("https://transifex.com/projects/p/passandroid")));
    }

    @Test
    public void testShareClick() {
        testThatNavigationDrawerOpens();
        Spoon.screenshot(rule.getActivity(), "open_drawer");

        intending(hasAction(Intent.ACTION_SEND)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

        onView(withText(R.string.nav_share)).perform(click());

        intended(allOf(hasAction(Intent.ACTION_SEND), hasType("text/plain")));
    }

    @Test
    public void testSettings() {
        testThatNavigationDrawerOpens();
        Spoon.screenshot(rule.getActivity(), "open_drawer");

        onView(withText(R.string.nav_settings)).perform(click());

        intended(hasComponent(PreferenceActivity.class.getName()));
    }
}
