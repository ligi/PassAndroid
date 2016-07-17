package org.ligi.passandroid;


import android.graphics.Bitmap;
import android.support.test.filters.SmallTest;
import android.test.InstrumentationTestCase;
import com.google.zxing.common.BitMatrix;
import org.ligi.passandroid.helper.BarcodeHelper;
import org.ligi.passandroid.model.pass.PassBarCodeFormat;
import static org.assertj.core.api.Assertions.assertThat;

public class TheBarcodeHelper extends InstrumentationTestCase {

    @SmallTest
    public void testQRBitMatrixHasCorrectSize() throws Exception {
        testBitMatrixSizeIsSane(PassBarCodeFormat.QR_CODE);
    }

    @SmallTest
    public void testQRBitmapHasCorrectSize() throws Exception {
        testBitmapSizeIsSane(PassBarCodeFormat.QR_CODE);
    }

    @SmallTest
    public void testPDF417BitmapHasCorrectSize() {
        testBitmapSizeIsSane(PassBarCodeFormat.PDF_417);
    }

    @SmallTest
    public void testPDF417BitMatrixHasCorrectSize() {
        testBitMatrixSizeIsSane(PassBarCodeFormat.PDF_417);
    }

    @SmallTest
    public void testAZTECBitmapHasCorrectSize() {
        testBitmapSizeIsSane(PassBarCodeFormat.AZTEC);
    }

    @SmallTest
    public void testAZTECBitMatrixHasCorrectSize() {
        testBitMatrixSizeIsSane(PassBarCodeFormat.AZTEC);
    }

    public void testBitMatrixSizeIsSane(final PassBarCodeFormat format) {
        try {
            BitMatrix tested = BarcodeHelper.getBitMatrix("foo-data", format);

            assertThat(tested.getWidth()).isGreaterThan(3);
        } catch (Exception e) {
            fail("could not create barcode " + e);
        }

    }

    public void testBitmapSizeIsSane(final PassBarCodeFormat format) {
        try {
            Bitmap tested2 = BarcodeHelper.generateBarCodeBitmap("foo-data", format);

            assertNotNull(tested2);
            assertThat(tested2.getWidth()).isGreaterThan(3);
        } catch (Exception e) {
            fail("could not create barcode " + e);
        }

    }
}
