package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import com.squareup.spoon.Spoon;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.PassType;
import org.ligi.passandroid.ui.PassEditActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;

@TargetApi(14)
public class ThePassEditActivity {

    @Rule
    public IntentsTestRule<PassEditActivity> rule = new IntentsTestRule<>(PassEditActivity.class, false, false);

    @Inject
    PassStore passStore;


    @Before
    public void setUp() {
        TestApp.component().inject(this);

        rule.launchActivity(null);
    }

    @Test
    public void testSetToEventWorks() {

        onView(withId(R.id.categoryView)).perform(click());

        onView(withText(R.string.select_category_dialog_title)).perform(click());
        onView(withText(R.string.category_event)).perform(click());
        assertThat(passStore.getCurrentPass().getType()).isEqualTo(PassType.EVENT);

        Spoon.screenshot(rule.getActivity(), "edit_set_event");
    }

    @Test
    public void testSetToCouponWorks() {
        onView(withId(R.id.categoryView)).perform(click());

        onView(withText(R.string.select_category_dialog_title)).perform(click());
        onView(withText(R.string.category_coupon)).perform(click());
        assertThat(passStore.getCurrentPass().getType()).isEqualTo(PassType.COUPON);

        Spoon.screenshot(rule.getActivity(), "edit_set_coupon");
    }

    @Test
    public void testSetDescriptionWorks() {

        onView(withId(R.id.passTitle)).perform(clearText(), typeText("test description"));
        assertThat(passStore.getCurrentPass().getDescription()).isEqualTo("test description");

        Spoon.screenshot(rule.getActivity(), "edit_set_description");
    }


    @Test
    public void testColorWheelIsThere() {

        onView(withId(R.id.categoryView)).perform(click());
        onView(withText(R.string.change_color_dialog_title)).perform(click());

        onView(withId(R.id.colorPicker)).check(matches(isDisplayed()));

        Spoon.screenshot(rule.getActivity(), "edit_set_color");
    }


    @Test
    public void testAddAbortFooterImagePick() {
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

        onView(withId(R.id.add_footer)).perform(scrollTo(), click());

        intended(hasAction(Intent.ACTION_CHOOSER));
    }

    @Test
    public void testAddAbortStripImagePick() {
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

        onView(withId(R.id.add_strip)).perform(scrollTo(), click());

        intended(hasAction(Intent.ACTION_CHOOSER));
    }

    @Test
    public void testAddAbortLogoImagePick() {

        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

        onView(withId(R.id.add_logo)).perform(scrollTo(), click());

        intended(hasAction(Intent.ACTION_CHOOSER));
    }

}
