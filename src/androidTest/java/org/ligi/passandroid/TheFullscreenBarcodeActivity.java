package org.ligi.passandroid;

import android.graphics.drawable.BitmapDrawable;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.squareup.spoon.Spoon;

import org.ligi.passandroid.helper.BarcodeDecoder;
import org.ligi.passandroid.injections.FixedPassBook;
import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.Passbook;
import org.ligi.passandroid.ui.FullscreenBarcodeActivity;
import org.ligi.tracedroid.TraceDroid;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.fest.assertions.api.Assertions.assertThat;

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

        final ImageView viewById = ButterKnife.findById(getActivity(), R.id.fullscreen_image);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) viewById.getDrawable();
        assertThat(BarcodeDecoder.decodeBitmap(bitmapDrawable.getBitmap())).isEqualTo("foo");
    }


    @MediumTest
    public void test_that_aztec_barcode_works() {
        final FixedPassBook pass = new FixedPassBook();
        pass.barcodeFormat = BarcodeFormat.AZTEC;
        App.getPassStore().setCurrentPass(pass);
        getActivity();
        onView(withId(R.id.fullscreen_image)).check(matches(isDisplayed()));

        Spoon.screenshot(getActivity(), "aztec_barcode");

        final ImageView viewById = ButterKnife.findById(getActivity(), R.id.fullscreen_image);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) viewById.getDrawable();
        assertThat(BarcodeDecoder.decodeBitmap(bitmapDrawable.getBitmap())).isEqualTo("foo");
    }


    @MediumTest
    public void test_that_qr_barcode_works() {
        final FixedPassBook pass = new FixedPassBook();
        pass.barcodeFormat = BarcodeFormat.QR_CODE;
        App.getPassStore().setCurrentPass(pass);
        getActivity();
        onView(withId(R.id.fullscreen_image)).check(matches(isDisplayed()));

        Spoon.screenshot(getActivity(), "qr_barcode");

        final ImageView viewById = ButterKnife.findById(getActivity(), R.id.fullscreen_image);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) viewById.getDrawable();
        assertThat(BarcodeDecoder.decodeBitmap(bitmapDrawable.getBitmap())).isEqualTo("foo");
    }

}
