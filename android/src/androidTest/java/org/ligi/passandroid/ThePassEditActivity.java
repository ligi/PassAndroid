package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.MediumTest;
import com.squareup.spoon.Spoon;
import javax.inject.Inject;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.PassType;
import org.ligi.passandroid.ui.PassEditActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;

@TargetApi(14)
public class ThePassEditActivity extends BaseIntegration<PassEditActivity> {

    @Inject
    PassStore passStore;

    public ThePassEditActivity() {
        super(PassEditActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final TestComponent build = DaggerTestComponent.create();
        build.inject(this);
        App.setComponent(build);

        getActivity();
    }

    @MediumTest
    public void testSetToEventWorks() {

        onView(withId(R.id.categoryView)).perform(click());

        onView(withText(R.string.category_event)).perform(click());
        assertThat(passStore.getCurrentPass().getType()).isEqualTo(PassType.EVENT);

        Spoon.screenshot(getActivity(), "edit_set_event");
    }

    @MediumTest
    public void testSetToCouponWorks() {
        onView(withId(R.id.categoryView)).perform(click());

        onView(withText(R.string.category_coupon)).perform(click());
        assertThat(passStore.getCurrentPass().getType()).isEqualTo(PassType.COUPON);

        Spoon.screenshot(getActivity(), "edit_set_coupon");
    }

    @MediumTest
    public void testSetDescriptionWorks() {

        onView(withId(R.id.title)).perform(clearText(),typeText("test description"));
        assertThat(passStore.getCurrentPass().getDescription()).isEqualTo("test description");

        Spoon.screenshot(getActivity(), "edit_set_description");
    }


    @MediumTest
    public void testColorWheelIsThere() {

        onView(withId(R.id.categoryView)).perform(click());
        onView(withText(R.string.button_text_change_color)).perform(click());

        onView(withId(R.id.colorPicker)).check(matches(isDisplayed()));

        Spoon.screenshot(getActivity(), "edit_set_color");
    }

}
