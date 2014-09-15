package org.ligi.passandroid;

import android.graphics.drawable.BitmapDrawable;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.squareup.spoon.Spoon;

import org.ligi.passandroid.helper.BarcodeDecoder;
import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.BarCode;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.ui.FullscreenBarcodeActivity;
import org.ligi.tracedroid.TraceDroid;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.assertj.core.api.Assertions.assertThat;

public class TheFullscreenBarcodeActivity extends BaseIntegration<FullscreenBarcodeActivity> {

    public static final String BARCODE_MESSAGE = "foo";

    public TheFullscreenBarcodeActivity() {
        super(FullscreenBarcodeActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final ArrayList<Pass> list = new ArrayList<Pass>() {{
            add(new PassImpl());
        }};

        App.replacePassStore(new FixedPassListPassStore(list));
        App.getPassStore().setCurrentPass(list.get(0));

        TraceDroid.deleteStacktraceFiles();
    }

    @MediumTest
    public void testPDF417BarcodeIsShown() {
        testWithBarcodeFormat(BarcodeFormat.PDF_417);

        Spoon.screenshot(getActivity(), "pdf417_barcode");
    }


    @MediumTest
    public void testAztecBarcodeIsShown() {
        testWithBarcodeFormat(BarcodeFormat.AZTEC);

        Spoon.screenshot(getActivity(), "aztec_barcode");
    }


    @MediumTest
    public void testQRCodeIsShown() {
        testWithBarcodeFormat(BarcodeFormat.QR_CODE);

        Spoon.screenshot(getActivity(), "qr_barcode");
    }

    private void testWithBarcodeFormat(BarcodeFormat format) {
        final PassImpl pass = new PassImpl();
        pass.setBarCode(new BarCode(format, BARCODE_MESSAGE));
        App.getPassStore().setCurrentPass(pass);
        getActivity();
        onView(withId(R.id.fullscreen_image)).check(matches(isDisplayed()));

        final ImageView viewById = ButterKnife.findById(getActivity(), R.id.fullscreen_image);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) viewById.getDrawable();
        assertThat(BarcodeDecoder.decodeBitmap(bitmapDrawable.getBitmap())).isEqualTo(BARCODE_MESSAGE);
    }

}
