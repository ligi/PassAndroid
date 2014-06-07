package org.ligi.passandroid;


import android.graphics.Bitmap;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;

import org.ligi.passandroid.helper.BarcodeHelper;

import static org.fest.assertions.api.Assertions.assertThat;

public class TheBarcodeHelper extends BaseTest {

    public static final BarcodeFormat PDF_417 = BarcodeFormat.PDF_417;

    @SmallTest
    public void QRShouldWork() throws Exception {
        testFormat(BarcodeFormat.QR_CODE);
    }

    @SmallTest
    public void PDF417ShouldWork() {
        testFormat(BarcodeFormat.PDF_417);
    }

    @SmallTest
    public void AZTECFormatShouldWork() {
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
