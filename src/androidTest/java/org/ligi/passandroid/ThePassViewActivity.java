package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.MediumTest;

import com.google.common.base.Optional;

import org.joda.time.DateTime;
import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.ui.PassViewActivity;

import java.util.ArrayList;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

@TargetApi(14)
public class ThePassViewActivity extends BaseIntegration<PassViewActivity> {

    private PassImpl act_pass;

    public ThePassViewActivity() {
        super(PassViewActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final ArrayList<Pass> list = new ArrayList<Pass>() {{
            act_pass = new PassImpl();
            act_pass.setDescription("foo");
            add(act_pass);
        }};

        App.replacePassStore(new FixedPassListPassStore(list));
        App.getPassStore().setCurrentPass(App.getPassStore().getPassbookAt(0));

    }

    @MediumTest
    public void testThatDescriptionIsThere() {
        getActivity();

        onView(withText(act_pass.getDescription())).check(matches(isDisplayed()));
    }

    @MediumTest
    public void testDateIsGoneWhenPassbookHasNoDate() {

        getActivity();

        onView(withId(R.id.date)).check(matches(not(isDisplayed())));
    }


    @MediumTest
    public void testDateIsThereWhenPassbookHasDate() {
        act_pass.setRelevantDate(Optional.of(new DateTime()));
        getActivity();

        onView(withId(R.id.date)).check(matches(isDisplayed()));
    }

    @MediumTest
    public void testLinkToCalendarIsThereWhenPassbookHasDate() {
        act_pass.setRelevantDate(Optional.of(new DateTime()));
        getActivity();

        onView(withId(R.id.addCalendar)).check(matches(isDisplayed()));
    }


    @MediumTest
    public void testLinkToCalendarIsNotThereWhenPassbookHasNoDate() {
        getActivity();

        onView(withId(R.id.addCalendar)).check(matches(not(isDisplayed())));
    }

    @MediumTest
    public void testClickOnBarcodeOpensFullscreenImage() {
        getActivity();
        onView(withId(R.id.barcode_img)).perform(click());

        onView(withId(R.id.fullscreen_image)).check(matches(isDisplayed()));
    }


}
