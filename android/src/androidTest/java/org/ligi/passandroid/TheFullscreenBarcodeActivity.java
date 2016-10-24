package org.ligi.passandroid;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import butterknife.ButterKnife;
import com.squareup.spoon.Spoon;
import java.util.UUID;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.gobandroid_hd.base.PassandroidTestRule;
import org.ligi.passandroid.helper.BarcodeDecoder;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.BarCode;
import org.ligi.passandroid.model.pass.PassBarCodeFormat;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.ui.FullscreenBarcodeActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.assertj.core.api.Assertions.assertThat;

public class TheFullscreenBarcodeActivity {

    @Rule
    public PassandroidTestRule<FullscreenBarcodeActivity> rule = new PassandroidTestRule<>(FullscreenBarcodeActivity.class, false);

    @Inject
    PassStore passStore;

    private static final String BARCODE_MESSAGE = "2323";

    @Before
    public void setUp() {
        TestApp.component().inject(this);
    }

    @Test
    public void testPDF417BarcodeIsShown() {
        testWithBarcodeFormat(PassBarCodeFormat.PDF_417);

        Spoon.screenshot(rule.getActivity(), "pdf417_barcode");
    }

    @Test
    public void testAztecBarcodeIsShown() {
        testWithBarcodeFormat(PassBarCodeFormat.AZTEC);

        Spoon.screenshot(rule.getActivity(), "aztec_barcode");
    }


    @Test
    public void testQRCodeIsShown() {
        testWithBarcodeFormat(PassBarCodeFormat.QR_CODE);

        Spoon.screenshot(rule.getActivity(), "qr_barcode");
    }

    @Test
    public void testCode128CodeIsShown() {
        testWithBarcodeFormat(PassBarCodeFormat.CODE_128);

        Spoon.screenshot(rule.getActivity(), "code128_barcode");
    }


    @Test
    public void testCode39CodeIsShown() {
        testWithBarcodeFormat(PassBarCodeFormat.CODE_39);

        Spoon.screenshot(rule.getActivity(), "code39_barcode");
    }


    private void testWithBarcodeFormat(final PassBarCodeFormat format) {
        final PassImpl pass = new PassImpl(UUID.randomUUID().toString());
        pass.setBarCode(new BarCode(format, BARCODE_MESSAGE));

        passStore.setCurrentPass(pass);

        rule.launchActivity(null);
        onView(withId(R.id.fullscreen_barcode)).check(matches(isDisplayed()));

        final ImageView viewById = ButterKnife.findById(rule.getActivity(), R.id.fullscreen_barcode);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) viewById.getDrawable();
        final Bitmap bitmap = bitmapDrawable.getBitmap();

        final Bitmap bitmapToTest;
        if (format == PassBarCodeFormat.AZTEC) {
            // not sure why - but for the decoder to pick up AZTEC it must have moar pixelz - smells like a zxing bug
            bitmapToTest = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 2, bitmap.getHeight() * 2, false);
        } else {
            bitmapToTest = bitmap;
        }

        assertThat(BarcodeDecoder.INSTANCE.decodeBitmap(bitmapToTest)).isEqualTo(BARCODE_MESSAGE);
    }

}
