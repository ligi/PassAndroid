package org.ligi.passandroid;


import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;
import com.google.zxing.common.BitMatrix;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.passandroid.helper.BarcodeHelper;
import org.ligi.passandroid.model.pass.PassBarCodeFormat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@RunWith(AndroidJUnit4.class)
public class TheBarcodeHelper {

    @Test
    public void testQRBitMatrixHasCorrectSize() throws Exception {
        testBitMatrixSizeIsSane(PassBarCodeFormat.QR_CODE);
    }

    @Test
    public void testQRBitmapHasCorrectSize() throws Exception {
        testBitmapSizeIsSane(PassBarCodeFormat.QR_CODE);
    }

    @Test
    public void testPDF417BitmapHasCorrectSize() {
        testBitmapSizeIsSane(PassBarCodeFormat.PDF_417);
    }

    @Test
    public void testPDF417BitMatrixHasCorrectSize() {
        testBitMatrixSizeIsSane(PassBarCodeFormat.PDF_417);
    }

    @Test
    public void testAZTECBitmapHasCorrectSize() {
        testBitmapSizeIsSane(PassBarCodeFormat.AZTEC);
    }

    @Test
    public void testAZTECBitMatrixHasCorrectSize() {
        testBitMatrixSizeIsSane(PassBarCodeFormat.AZTEC);
    }

    public void testBitMatrixSizeIsSane(final PassBarCodeFormat format) {
        try {
            BitMatrix tested = BarcodeHelper.getBitMatrix("foo-data", format);

            assertThat(tested.getWidth()).isGreaterThan(3);
        } catch (Exception e) {
            fail("could not create barcode", e);
        }

    }

    public void testBitmapSizeIsSane(final PassBarCodeFormat format) {
        try {
            Bitmap tested2 = BarcodeHelper.generateBarCodeBitmap("foo-data", format);

            assert tested2 != null;
            assertThat(tested2.getWidth()).isGreaterThan(3);
        } catch (Exception e) {
            fail("could not create barcode" ,e);
        }

    }
}
