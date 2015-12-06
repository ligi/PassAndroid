package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.ui.PassEditActivity;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ligi.passandroid.steps.PassEditSteps.goToColor;
import static org.ligi.passandroid.steps.PassEditSteps.goToMetaData;

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
        onView(withText("Event")).perform(click());
        assertThat(passStore.getCurrentPass().getType()).isEqualTo("eventTicket");

        Spoon.screenshot(getActivity(), "edit_set_event");
    }

    @MediumTest
    public void testSetToCouponWorks() {
        onView(withText("Coupon")).perform(click());
        assertThat(passStore.getCurrentPass().getType()).isEqualTo("coupon");

        Spoon.screenshot(getActivity(), "edit_set_coupon");
    }

    @MediumTest
    public void testSetDescriptionWorks() {
        goToMetaData();

        onView(withId(R.id.descriptionEdit)).perform(clearText(),typeText("test description"));
        assertThat(passStore.getCurrentPass().getDescription()).isEqualTo("test description");

        Spoon.screenshot(getActivity(), "edit_set_description");
    }


    @MediumTest
    public void testColorWheelIsThere() {
        goToColor();

        onView(withId(R.id.colorPicker)).check(matches(isDisplayed()));

        Spoon.screenshot(getActivity(), "edit_set_color");
    }

}
