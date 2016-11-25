package org.ligi.passandroid;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import java.util.UUID;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.passandroid.helper.BarcodeDecoder;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.BarCode;
import org.ligi.passandroid.model.pass.PassBarCodeFormat;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.ui.FullscreenBarcodeActivity;
import org.ligi.trulesk.TruleskIntentRule;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ligi.passandroid.model.pass.PassBarCodeFormat.AZTEC;
import static org.ligi.passandroid.model.pass.PassBarCodeFormat.CODE_128;
import static org.ligi.passandroid.model.pass.PassBarCodeFormat.CODE_39;
import static org.ligi.passandroid.model.pass.PassBarCodeFormat.PDF_417;
import static org.ligi.passandroid.model.pass.PassBarCodeFormat.QR_CODE;

public class TheFullscreenBarcodeActivity {

    @Rule
    public TruleskIntentRule<FullscreenBarcodeActivity> rule = new TruleskIntentRule<>(FullscreenBarcodeActivity.class, false);

    @Inject
    PassStore passStore;

    private static final String BARCODE_MESSAGE = "2323";

    @Before
    public void setUp() {
        TestApp.component().inject(this);
    }

    @Test
    public void testPDF417BarcodeIsShown() {
        testWithBarcodeFormat(PDF_417);

        rule.screenShot("pdf417_barcode");
    }

    @Test
    public void testAztecBarcodeIsShown() {
        testWithBarcodeFormat(AZTEC);

        rule.screenShot("aztec_barcode");
    }


    @Test
    public void testQRCodeIsShown() {
        testWithBarcodeFormat(QR_CODE);

        rule.screenShot("qr_barcode");
    }

    @Test
    public void testCode128CodeIsShown() {
        testWithBarcodeFormat(CODE_128);

        rule.screenShot("code128_barcode");
    }


    @Test
    public void testCode39CodeIsShown() {
        testWithBarcodeFormat(CODE_39);

        rule.screenShot("code39_barcode");
    }


    private void testWithBarcodeFormat(final PassBarCodeFormat format) {
        final PassImpl pass = new PassImpl(UUID.randomUUID().toString());
        pass.setBarCode(new BarCode(format, BARCODE_MESSAGE));

        passStore.setCurrentPass(pass);

        rule.launchActivity(null);
        onView(withId(R.id.fullscreen_barcode)).check(matches(isDisplayed()));

        final ImageView viewById = (ImageView) rule.getActivity().findViewById(R.id.fullscreen_barcode);
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
