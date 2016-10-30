package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.support.test.rule.ActivityTestRule;
import java.util.ArrayList;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.BarCode;
import org.ligi.passandroid.model.pass.PassBarCodeFormat;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.model.pass.PassLocation;
import org.ligi.passandroid.ui.PassViewActivity;
import org.threeten.bp.ZonedDateTime;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

@TargetApi(14)
public class ThePassViewActivity {

    @Inject
    PassStore passStore;

    PassImpl act_pass;

    @Rule
    public ActivityTestRule<PassViewActivity> rule = new ActivityTestRule<>(PassViewActivity.class, false, false);

    @Before
    public void setUp() {
        TestApp.component().inject(this);
        act_pass = (PassImpl) passStore.getCurrentPass();
    }

    @Test
    public void testThatDescriptionIsThere() {
        rule.launchActivity(null);

        onView(withText(act_pass.getDescription())).check(matches(isDisplayed()));
    }

    @Test
    public void testDateIsGoneWhenPassbookHasNoDate() {
        act_pass.setValidTimespans(new ArrayList<PassImpl.TimeSpan>());
        rule.launchActivity(null);

        onView(withId(R.id.date)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testEverythingWorksWhenWeHaveSomeLocation() {
        final ArrayList<PassLocation> timeSpen = new ArrayList<>();
        timeSpen.add(new PassLocation());
        act_pass.setLocations(timeSpen);
        rule.launchActivity(null);

        onView(withId(R.id.date)).check(matches(not(isDisplayed())));
    }


    @Test
    public void testDateIsThereWhenPassbookHasDate() {
        act_pass.setCalendarTimespan(new PassImpl.TimeSpan(ZonedDateTime.now(), null, null));
        rule.launchActivity(null);

        onView(withId(R.id.date)).check(matches(isDisplayed()));
    }

    @Test
    public void testLinkToCalendarIsThereWhenPassbookHasDate() {
        act_pass.setCalendarTimespan(new PassImpl.TimeSpan(ZonedDateTime.now(), null, null));
        rule.launchActivity(null);

        onView(withText(R.string.pass_to_calendar)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickOnCalendarWithExpirationDateGivesWarning() {
        final ArrayList<PassImpl.TimeSpan> validTimespans = new ArrayList<>();
        validTimespans.add(new PassImpl.TimeSpan(null, ZonedDateTime.now().minusHours(12), null));
        act_pass.setValidTimespans(validTimespans);
        act_pass.setCalendarTimespan(null);
        rule.launchActivity(null);

        onView(withText(R.string.pass_to_calendar)).perform(click());

        onView(withText(R.string.expiration_date_to_calendar_warning_message)).check(matches(isDisplayed()));
    }

    @Test
    public void testThatTheDialogCanBeDismissed() {
        testClickOnCalendarWithExpirationDateGivesWarning();

        onView(withText(android.R.string.cancel)).perform(click());

        onView(withText(R.string.expiration_date_to_calendar_warning_message)).check(doesNotExist());
    }

    @Test
    public void testLinkToCalendarIsNotThereWhenPassbookHasNoDate() {
        act_pass.setValidTimespans(new ArrayList<PassImpl.TimeSpan>());
        rule.launchActivity(null);

        onView(withText(R.string.pass_to_calendar)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testClickOnBarcodeOpensFullscreenImage() {
        act_pass.setBarCode(new BarCode(PassBarCodeFormat.QR_CODE, "foo"));
        rule.launchActivity(null);
        onView(withId(R.id.barcode_img)).perform(click());

        onView(withId(R.id.fullscreen_barcode)).check(matches(isDisplayed()));
    }


    @Test
    public void testZoomControlsAreThereWithBarcode() {
        act_pass.setBarCode(new BarCode(PassBarCodeFormat.AZTEC, "foo"));
        rule.launchActivity(null);

        onView(withId(R.id.zoomIn)).check(matches(isDisplayed()));
        onView(withId(R.id.zoomIn)).check(matches(isDisplayed()));
    }

    @Test
    public void testZoomControlsAreGoneWithoutBarcode() {
        act_pass.setBarCode(null);
        rule.launchActivity(null);

        onView(withId(R.id.zoomIn)).check(matches(not(isDisplayed())));
        onView(withId(R.id.zoomIn)).check(matches(not(isDisplayed())));
    }

}
