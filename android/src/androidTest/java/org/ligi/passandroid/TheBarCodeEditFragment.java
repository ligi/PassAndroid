package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.MediumTest;

import com.google.zxing.BarcodeFormat;
import com.squareup.spoon.Spoon;

import org.ligi.passandroid.model.PassImpl;
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

@TargetApi(14)
public class TheBarCodeEditFragment extends BaseIntegration<PassEditActivity> {

    @Inject
    PassStore passStore;

    PassImpl currentPass;

    public TheBarCodeEditFragment() {
        super(PassEditActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final TestComponent build = DaggerTestComponent.create();
        build.inject(this);
        App.setComponent(build);

        currentPass = (PassImpl) (passStore.getCurrentPass());
    }

    @MediumTest
    public void testNullBarcodeShowButtonAppears() {

        currentPass.setBarCode(null);
        getActivity();
        goToBarCode();

        Spoon.screenshot(getActivity(), "no_barcode");

        onView(withId(R.id.barcodeAddButton)).check(matches(isDisplayed()));
    }


    @MediumTest
    public void testCreateBarcodeDefaultsToQR() {

        currentPass.setBarCode(null);
        getActivity();
        goToBarCode();


        onView(withId(R.id.barcodeAddButton)).perform(click());

        assertThat(currentPass.getBarCode().getFormat()).isEqualTo(BarcodeFormat.QR_CODE);
    }


    @MediumTest
    public void testCanSetToQR() {
        getActivity();
        goToBarCode();

        onView(withText("QR")).perform(click());

        assertThat(currentPass.getBarCode().getFormat()).isEqualTo(BarcodeFormat.QR_CODE);
        Spoon.screenshot(getActivity(), "edit_set_qr");
    }


    @MediumTest
    public void testCanSetToPDF417() {
        getActivity();
        goToBarCode();

        onView(withText("PDF417")).perform(click());

        assertThat(currentPass.getBarCode().getFormat()).isEqualTo(BarcodeFormat.PDF_417);
        Spoon.screenshot(getActivity(), "edit_set_pdf417");
    }


    @MediumTest
    public void testCanSetToAZTEC() {
        getActivity();
        goToBarCode();

        onView(withText("AZTEC")).perform(click());

        assertThat(currentPass.getBarCode().getFormat()).isEqualTo(BarcodeFormat.AZTEC);
        Spoon.screenshot(getActivity(), "edit_set_aztec");
    }



    @MediumTest
    public void testCanSetMessage() {
        getActivity();
        goToBarCode();
        onView(withId(R.id.messageInput)).perform(clearText());
        onView(withId(R.id.messageInput)).perform(typeText("msg foo txt ;-)"));

        assertThat(passStore.getCurrentPass().getBarCode().getMessage()).isEqualTo("msg foo txt ;-)");
        Spoon.screenshot(getActivity(), "edit_set_msg");
    }


    @MediumTest
    public void testCanSetAltMessage() {
        getActivity();
        goToBarCode();

        onView(withId(R.id.alternativeMessageInput)).perform(scrollTo());
        onView(withId(R.id.alternativeMessageInput)).perform(typeText("alt bar txt ;-)"));

        assertThat(passStore.getCurrentPass().getBarCode().getAlternativeText()).isEqualTo("alt bar txt ;-)");
        Spoon.screenshot(getActivity(), "edit_set_altmsg");
    }


    @MediumTest
    public void testThatRandomChangesMessage() {
        getActivity();
        goToBarCode();

        final String oldMessage = passStore.getCurrentPass().getBarCode().getMessage();
        onView(withId(R.id.randomButton)).perform(click());

        assertThat(oldMessage).isNotEqualTo(passStore.getCurrentPass().getBarCode().getMessage());
    }


}
