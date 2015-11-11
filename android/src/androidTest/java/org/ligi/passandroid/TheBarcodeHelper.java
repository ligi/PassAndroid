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
        testBitMatrixSizeIsSane(BarcodeFormat.QR_CODE);
    }

    @SmallTest
    public void testQRBitmapHasCorrectSize() throws Exception {
        testBitmapSizeIsSane(BarcodeFormat.QR_CODE);
    }

    @SmallTest
    public void testPDF417BitmapHasCorrectSize() {
        testBitmapSizeIsSane(BarcodeFormat.PDF_417);
    }

    @SmallTest
    public void testPDF417BitMatrixHasCorrectSize() {
        testBitMatrixSizeIsSane(BarcodeFormat.PDF_417);
    }

    @SmallTest
    public void testAZTECBitmapHasCorrectSize() {
        testBitmapSizeIsSane(BarcodeFormat.AZTEC);
    }

    @SmallTest
    public void testAZTECBitMatrixHasCorrectSize() {
        testBitMatrixSizeIsSane(BarcodeFormat.AZTEC);
    }

    public void testBitMatrixSizeIsSane(final BarcodeFormat format) {
        try {
            BitMatrix tested = BarcodeHelper.getBitMatrix("foo-data", format);

            assertThat(tested.getWidth()).isGreaterThan(3);
        } catch (Exception e) {
            fail("could not create barcode " + e);
        }

    }

    public void testBitmapSizeIsSane(final BarcodeFormat format) {
        try {
            Bitmap tested2 = BarcodeHelper.generateBarCodeBitmap("foo-data", format);

            assertNotNull(tested2);
            assertThat(tested2.getWidth()).isGreaterThan(3);
        } catch (Exception e) {
            fail("could not create barcode " + e);
        }

    }
}
