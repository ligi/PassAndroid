package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.MediumTest;

import com.google.zxing.BarcodeFormat;
import com.squareup.spoon.Spoon;

import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.ui.PassEditActivity;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ligi.passandroid.steps.PassEditSteps.goToBarCode;
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

    @MediumTest
    public void testCanSetToQR() {
        goToBarCode();

        onView(withText("QR")).perform(click());

        assertThat(passStore.getCurrentPass().getBarCode().getFormat()).isEqualTo(BarcodeFormat.QR_CODE);
        Spoon.screenshot(getActivity(), "edit_set_qr");
    }


    @MediumTest
    public void testCanSetToPDF417() {
        goToBarCode();

        onView(withText("PDF417")).perform(click());

        assertThat(passStore.getCurrentPass().getBarCode().getFormat()).isEqualTo(BarcodeFormat.PDF_417);
        Spoon.screenshot(getActivity(), "edit_set_pdf417");
    }


    @MediumTest
    public void testCanSetToAZTEC() {
        goToBarCode();

        onView(withText("AZTEC")).perform(click());

        assertThat(passStore.getCurrentPass().getBarCode().getFormat()).isEqualTo(BarcodeFormat.AZTEC);
        Spoon.screenshot(getActivity(), "edit_set_aztec");
    }


    @MediumTest
    public void testCanSetMessage() {
        goToBarCode();
        onView(withId(R.id.messageInput)).perform(clearText());
        onView(withId(R.id.messageInput)).perform(typeText("msg foo txt ;-)"));

        assertThat(passStore.getCurrentPass().getBarCode().getMessage()).isEqualTo("msg foo txt ;-)");
        Spoon.screenshot(getActivity(), "edit_set_msg");
    }


    @MediumTest
    public void testCanSetAltMessage() {
        goToBarCode();

        onView(withId(R.id.alternativeMessageInput)).perform(scrollTo());
        onView(withId(R.id.alternativeMessageInput)).perform(typeText("alt bar txt ;-)"));

        assertThat(passStore.getCurrentPass().getBarCode().getAlternativeText()).isEqualTo("alt bar txt ;-)");
        Spoon.screenshot(getActivity(), "edit_set_altmsg");
    }


}
