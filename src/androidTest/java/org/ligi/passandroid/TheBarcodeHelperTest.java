package org.ligi.passandroid;


import android.app.Activity;
import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;

import org.ligi.passandroid.helper.BarcodeHelper;

import static org.fest.assertions.api.Assertions.assertThat;

public class TheBarcodeHelperTest extends ActivityInstrumentationTestCase2<Activity> {

    public static final BarcodeFormat PDF_417 = BarcodeFormat.PDF_417;

    public TheBarcodeHelperTest() {
        super(Activity.class);
    }

    @SmallTest
    public void test_barcode_qr_format_should_have_correct_output_size() throws Exception {
        testFormat(BarcodeFormat.QR_CODE);
    }

    @SmallTest
    public void test_barcode_pdf417_format_should_have_correct_output_size() {
        testFormat(BarcodeFormat.PDF_417);
    }

    @SmallTest
    public void barcode_aztec_format_writer_should_have_correct_output_size() {
        testFormat(BarcodeFormat.AZTEC);

    }

    public void testFormat(BarcodeFormat format) {
        try {
            BitMatrix tested = BarcodeHelper.getBitMatrix("foo-data", format, 42);

            assertThat(tested.getWidth()).isGreaterThanOrEqualTo(42);

            Bitmap tested2 = BarcodeHelper.generateBarCodeBitmap("foo-data", format, 42);

            assertThat(tested2.getWidth()).isGreaterThanOrEqualTo(42);
        } catch (Exception e) {
            fail("could not create barcode " + e);
        }

    }
}
