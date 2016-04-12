package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.MediumTest;
import com.squareup.spoon.Spoon;
import javax.inject.Inject;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.PassBarCodeFormat;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.ui.PassEditActivity;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
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

@TargetApi(14)
public class TheBarCodeEditing extends BaseIntegration<PassEditActivity> {

    @Inject
    PassStore passStore;

    PassImpl currentPass;

    public TheBarCodeEditing() {
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

        Spoon.screenshot(getActivity(), "no_barcode");

        onView(withId(R.id.add_barcode_button)).perform(scrollTo());
        onView(withId(R.id.add_barcode_button)).check(matches(isDisplayed()));
    }


    @MediumTest
    public void testCreateBarcodeDefaultsToQR() {

        currentPass.setBarCode(null);
        getActivity();

        onView(withId(R.id.add_barcode_button)).perform(scrollTo(),click());

        closeSoftKeyboard();

        onView(withText(android.R.string.ok)).perform(click());

        assertThat(currentPass.getBarCode().getFormat()).isEqualTo(PassBarCodeFormat.QR_CODE);
    }


    @MediumTest
    public void testCanSetToQR() {
        getActivity();

        onView(withId(R.id.barcode_img)).perform(click());

        onView(withText(R.string.barcode_edit_qr)).perform(click());

        closeSoftKeyboard();

        onView(withText(android.R.string.ok)).perform(click());

        assertThat(currentPass.getBarCode().getFormat()).isEqualTo(PassBarCodeFormat.QR_CODE);
        Spoon.screenshot(getActivity(), "edit_set_qr");
    }


    @MediumTest
    public void testCanSetToPDF417() {
        getActivity();

        onView(withId(R.id.barcode_img)).perform(click());

        onView(withText(R.string.barcode_edit_pdf417)).perform(click());

        closeSoftKeyboard();

        onView(withText(android.R.string.ok)).perform(click());

        assertThat(currentPass.getBarCode().getFormat()).isEqualTo(PassBarCodeFormat.PDF_417);
        Spoon.screenshot(getActivity(), "edit_set_pdf417");
    }


    @MediumTest
    public void testCanSetToAZTEC() {
        getActivity();

        onView(withId(R.id.barcode_img)).perform(click());

        onView(withText(R.string.barcode_edit_aztec)).perform(click());

        assertThat(currentPass.getBarCode().getFormat()).isEqualTo(PassBarCodeFormat.AZTEC);
        Spoon.screenshot(getActivity(), "edit_set_aztec");
    }



    @MediumTest
    public void testCanSetMessage() {
        getActivity();

        onView(withId(R.id.barcode_img)).perform(click());

        onView(withId(R.id.messageInput)).perform(clearText());
        onView(withId(R.id.messageInput)).perform(typeText("msg foo txt ;-)"));

        closeSoftKeyboard();

        onView(withText(android.R.string.ok)).perform(click());

        assertThat(passStore.getCurrentPass().getBarCode().getMessage()).isEqualTo("msg foo txt ;-)");
        Spoon.screenshot(getActivity(), "edit_set_msg");
    }


    @MediumTest
    public void testCanSetAltMessage() {
        getActivity();

        onView(withId(R.id.barcode_img)).perform(click());

        onView(withId(R.id.alternativeMessageInput)).perform(clearText());
        onView(withId(R.id.alternativeMessageInput)).perform(typeText("alt bar txt ;-)"));

        closeSoftKeyboard();

        onView(withText(android.R.string.ok)).perform(click());

        assertThat(passStore.getCurrentPass().getBarCode().getAlternativeText()).isEqualTo("alt bar txt ;-)");
        Spoon.screenshot(getActivity(), "edit_set_altmsg");
    }


    @MediumTest
    public void testThatRandomChangesMessage() {
        getActivity();

        onView(withId(R.id.barcode_img)).perform(click());

        final String oldMessage = passStore.getCurrentPass().getBarCode().getMessage();
        onView(withId(R.id.randomButton)).perform(click());

        closeSoftKeyboard();

        onView(withText(android.R.string.ok)).perform(click());

        assertThat(oldMessage).isNotEqualTo(passStore.getCurrentPass().getBarCode().getMessage());
    }


}
