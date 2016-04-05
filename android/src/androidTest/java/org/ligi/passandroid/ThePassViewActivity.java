package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.MediumTest;
import com.google.zxing.BarcodeFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.BarCode;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.ui.PassViewActivity;
import org.threeten.bp.ZonedDateTime;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@TargetApi(14)
public class ThePassViewActivity extends BaseIntegration<PassViewActivity> {

    @Inject
    PassStore passStore;

    PassImpl act_pass;

    public ThePassViewActivity() {
        super(PassViewActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final TestComponent newComponent = DaggerTestComponent.create();
        newComponent.inject(this);
        App.setComponent(newComponent);

        act_pass = (PassImpl) passStore.getCurrentPass();
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
        act_pass.setCalendarTimespan(new PassImpl.TimeSpan(ZonedDateTime.now(), null, null));
        getActivity();

        onView(withId(R.id.date)).check(matches(isDisplayed()));
    }

    @MediumTest
    public void testLinkToCalendarIsThereWhenPassbookHasDate() {
        act_pass.setCalendarTimespan(new PassImpl.TimeSpan(ZonedDateTime.now(), null, null));
        getActivity();

        onView(withId(R.id.addCalendar)).check(matches(isDisplayed()));
    }

    @MediumTest
    public void testClickOnCalendarWithExpirationDateGivesWarning() {
        final ArrayList<PassImpl.TimeSpan> validTimespans = new ArrayList<>();
        validTimespans.add(new PassImpl.TimeSpan(null, ZonedDateTime.now().minusHours(12), null));
        act_pass.setValidTimespans(validTimespans);
        act_pass.setCalendarTimespan(null);
        getActivity();

        onView(withId(R.id.addCalendar)).perform(click());

        onView(withText(R.string.expiration_date_to_calendar_warning_message)).check(matches(isDisplayed()));
    }

    @MediumTest
    public void testThatTheDialogCanBeDismissed() {
        testClickOnCalendarWithExpirationDateGivesWarning();

        onView(withText(android.R.string.cancel)).perform(click());

        onView(withText(R.string.expiration_date_to_calendar_warning_message)).check(doesNotExist());
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

        onView(withId(R.id.fullscreen_barcode)).check(matches(isDisplayed()));
    }


    @MediumTest
    public void testZoomControlsAreThereWithBarcode() {
        act_pass.setBarCode(new BarCode(BarcodeFormat.AZTEC, "foo"));
        getActivity();

        onView(withId(R.id.zoomIn)).check(matches(isDisplayed()));
        onView(withId(R.id.zoomIn)).check(matches(isDisplayed()));
    }

    @MediumTest
    public void testZoomControlsAreGoneWithoutBarcode() {
        act_pass.setBarCode(null);
        getActivity();

        onView(withId(R.id.zoomIn)).check(matches(not(isDisplayed())));
        onView(withId(R.id.zoomIn)).check(matches(not(isDisplayed())));
    }

}
