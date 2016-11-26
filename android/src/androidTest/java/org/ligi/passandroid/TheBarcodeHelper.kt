package org.ligi.passandroid


import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Fail.fail
import org.junit.Test
import org.ligi.passandroid.functions.generateBarCodeBitmap
import org.ligi.passandroid.functions.getBitMatrix
import org.ligi.passandroid.model.pass.PassBarCodeFormat

class TheBarcodeHelper {

    @Test
    fun testQRBitMatrixHasCorrectSize() {
        testBitMatrixSizeIsSane(PassBarCodeFormat.QR_CODE)
    }

    @Test
    fun testQRBitmapHasCorrectSize() {
        testBitmapSizeIsSane(PassBarCodeFormat.QR_CODE)
    }

    @Test
    fun testPDF417BitmapHasCorrectSize() {
        testBitmapSizeIsSane(PassBarCodeFormat.PDF_417)
    }

    @Test
    fun testPDF417BitMatrixHasCorrectSize() {
        testBitMatrixSizeIsSane(PassBarCodeFormat.PDF_417)
    }

    @Test
    fun testAZTECBitmapHasCorrectSize() {
        testBitmapSizeIsSane(PassBarCodeFormat.AZTEC)
    }

    @Test
    fun testAZTECBitMatrixHasCorrectSize() {
        testBitMatrixSizeIsSane(PassBarCodeFormat.AZTEC)
    }

    fun testBitMatrixSizeIsSane(format: PassBarCodeFormat) {
        try {
            val tested = getBitMatrix("foo-data", format)

            assertThat(tested.width).isGreaterThan(3)
        } catch (e: Exception) {
            fail("could not create barcode", e)
        }

    }

    fun testBitmapSizeIsSane(format: PassBarCodeFormat) {
        try {
            val tested2 = generateBarCodeBitmap("foo-data", format)!!

            assertThat(tested2.width).isGreaterThan(3)
        } catch (e: Exception) {
            fail("could not create barcode", e)
        }

    }
}
