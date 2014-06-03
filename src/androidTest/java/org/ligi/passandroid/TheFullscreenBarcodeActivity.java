package org.ligi.passandroid;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.google.zxing.BarcodeFormat;
import com.squareup.spoon.Spoon;

import org.ligi.passandroid.injections.FixedPassBook;
import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.Passbook;
import org.ligi.passandroid.ui.FullscreenBarcodeActivity;
import org.ligi.tracedroid.TraceDroid;

import java.util.ArrayList;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class TheFullscreenBarcodeActivity extends ActivityInstrumentationTestCase2<FullscreenBarcodeActivity> {

    public TheFullscreenBarcodeActivity() {
        super(FullscreenBarcodeActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final ArrayList<Passbook> list = new ArrayList<Passbook>() {{
            add(new FixedPassBook());
        }};

        App.replacePassStore(new FixedPassListPassStore(list));
        App.getPassStore().setCurrentPass(list.get(0));

        TraceDroid.deleteStacktraceFiles();

    }

    @MediumTest
    public void test_that_image_is_there() {
        final FixedPassBook pass = new FixedPassBook();
        pass.barcodeFormat = BarcodeFormat.PDF_417;
        App.getPassStore().setCurrentPass(pass);
        getActivity();
        onView(withId(R.id.fullscreen_image)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "pdf417_barcode");
    }


    @MediumTest
    public void test_that_aztec_barcode_works() {
        final FixedPassBook pass = new FixedPassBook();
        pass.barcodeFormat = BarcodeFormat.AZTEC;
        App.getPassStore().setCurrentPass(pass);
        getActivity();
        onView(withId(R.id.fullscreen_image)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "aztec_barcode");
    }


    @MediumTest
    public void test_that_qr_barcode_works() {
        final FixedPassBook pass = new FixedPassBook();
        pass.barcodeFormat = BarcodeFormat.QR_CODE;
        App.getPassStore().setCurrentPass(pass);
        getActivity();
        onView(withId(R.id.fullscreen_image)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "qr_barcode");
    }

}
