package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.MediumTest;

import com.google.zxing.BarcodeFormat;
import com.squareup.spoon.Spoon;

import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.BarCode;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.ui.PassEditActivity;

import java.util.ArrayList;
import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
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

    public ThePassEditActivity() {
        super(PassEditActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final ArrayList<Pass> list = new ArrayList<Pass>() {{
            final PassImpl object = new PassImpl();
            object.setBarCode(new BarCode(BarcodeFormat.QR_CODE, UUID.randomUUID().toString()));
            add(object);
        }};

        final FixedPassListPassStore newPassStore = new FixedPassListPassStore(list);
        newPassStore.setCurrentPass(list.get(0));
        App.replacePassStore(newPassStore);

        getActivity();
    }

    @MediumTest
    public void testSetToEventWorks() {
        onView(withText("Event")).perform(click());
        assertThat(App.getPassStore().getCurrentPass().get().getType()).isEqualTo("eventTicket");

        Spoon.screenshot(getActivity(), "edit_set_event");
    }

    @MediumTest
    public void testSetToCouponWorks() {
        onView(withText("Coupon")).perform(click());
        assertThat(App.getPassStore().getCurrentPass().get().getType()).isEqualTo("coupon");

        Spoon.screenshot(getActivity(), "edit_set_coupon");
    }

    @MediumTest
    public void testSetDescriptionWorks() {
        goToMetaData();

        onView(withId(R.id.descriptionEdit)).perform(typeText("test description"));
        assertThat(App.getPassStore().getCurrentPass().get().getDescription()).isEqualTo("test description");

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

        assertThat(App.getPassStore().getCurrentPass().get().getBarCode().getFormat()).isEqualTo(BarcodeFormat.QR_CODE);
        Spoon.screenshot(getActivity(), "edit_set_qr");
    }


    @MediumTest
    public void testCanSetToPDF417() {
        goToBarCode();

        onView(withText("PDF417")).perform(click());

        assertThat(App.getPassStore().getCurrentPass().get().getBarCode().getFormat()).isEqualTo(BarcodeFormat.PDF_417);
        Spoon.screenshot(getActivity(), "edit_set_pdf417");
    }


    @MediumTest
    public void testCanSetToAZTEC() {
        goToBarCode();

        onView(withText("AZTEC")).perform(click());

        assertThat(App.getPassStore().getCurrentPass().get().getBarCode().getFormat()).isEqualTo(BarcodeFormat.AZTEC);
        Spoon.screenshot(getActivity(), "edit_set_aztec");
    }


    @MediumTest
    public void testCanSetMessage() {
        goToBarCode();
        onView(withId(R.id.messageInput)).perform(clearText());
        onView(withId(R.id.messageInput)).perform(typeText("msg foo txt ;-)"));

        assertThat(App.getPassStore().getCurrentPass().get().getBarCode().getMessage()).isEqualTo("msg foo txt ;-)");
        Spoon.screenshot(getActivity(), "edit_set_msg");
    }


    @MediumTest
    public void testCanSetAltMessage() {
        goToBarCode();

        onView(withId(R.id.alternativeMessageInput)).perform(typeText("alt bar txt ;-)"));

        assertThat(App.getPassStore().getCurrentPass().get().getBarCode().getAlternativeText()).isEqualTo("alt bar txt ;-)");
        Spoon.screenshot(getActivity(), "edit_set_altmsg");
    }


}
