package org.ligi.passandroid;


import android.graphics.Bitmap;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;

import org.ligi.passandroid.helper.BarcodeHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class TheBarcodeHelper extends InstrumentationTestCase {

    @SmallTest
    public void testQRBitMatrixHasCorrectSize() throws Exception {
        testBitMatrixSize(BarcodeFormat.QR_CODE);
    }

    @SmallTest
    public void testQRBitmapHasCorrectSize() throws Exception {
        testBitmapSize(BarcodeFormat.QR_CODE);
    }

    @SmallTest
    public void testPDF417BitmapHasCorrectSize() {
        testBitmapSize(BarcodeFormat.PDF_417);
    }

    @SmallTest
    public void testPDF417BitMatrixHasCorrectSize() {
        testBitMatrixSize(BarcodeFormat.PDF_417);
    }

    @SmallTest
    public void testAZTECBitmapHasCorrectSize() {
        testBitmapSize(BarcodeFormat.AZTEC);
    }

    @SmallTest
    public void testAZTECBitMatrixHasCorrectSize() {
        testBitMatrixSize(BarcodeFormat.AZTEC);
    }

    public void testBitMatrixSize(final BarcodeFormat format) {
        try {
            BitMatrix tested = BarcodeHelper.getBitMatrix("foo-data", format, 42);

            assertThat(tested.getWidth()).isGreaterThanOrEqualTo(42);
        } catch (Exception e) {
            fail("could not create barcode " + e);
        }

    }

    public void testBitmapSize(final BarcodeFormat format) {
        try {
            Bitmap tested2 = BarcodeHelper.generateBarCodeBitmap("foo-data", format, 42);

            assertNotNull(tested2);
            assertThat(tested2.getWidth()).isGreaterThanOrEqualTo(42);
        } catch (Exception e) {
            fail("could not create barcode " + e);
        }

    }
}
