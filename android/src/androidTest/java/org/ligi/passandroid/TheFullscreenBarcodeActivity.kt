package org.ligi.passandroid

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.core.graphics.scale
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.functions.decodeBarCode
import org.ligi.passandroid.model.pass.BarCode
import org.ligi.passandroid.model.pass.PassBarCodeFormat
import org.ligi.passandroid.model.pass.PassBarCodeFormat.*
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.ui.FullscreenBarcodeActivity
import org.ligi.trulesk.TruleskIntentRule
import java.util.*

private const val BARCODE_MESSAGE = "2323"

class TheFullscreenBarcodeActivity {

    @get:Rule
    var rule = TruleskIntentRule(FullscreenBarcodeActivity::class.java, false)

    @Test
    fun testPDF417BarcodeIsShown() {
        testWithBarcodeFormat(PDF_417)

        rule.screenShot("pdf417_barcode")
    }

    @Test
    fun testAztecBarcodeIsShown() {
        testWithBarcodeFormat(AZTEC)

        rule.screenShot("aztec_barcode")
    }


    @Test
    fun testQRCodeIsShown() {
        testWithBarcodeFormat(QR_CODE)

        rule.screenShot("qr_barcode")
    }

    @Test
    fun testCode128CodeIsShown() {
        testWithBarcodeFormat(CODE_128)

        rule.screenShot("code128_barcode")
    }


    @Test
    fun testCode39CodeIsShown() {
        testWithBarcodeFormat(CODE_39)

        rule.screenShot("code39_barcode")
    }


    private fun testWithBarcodeFormat(format: PassBarCodeFormat) {
        val pass = PassImpl(UUID.randomUUID().toString())
        pass.barCode = BarCode(format, BARCODE_MESSAGE)

        TestApp.passStore.currentPass = pass

        rule.launchActivity(null)
        onView(withId(R.id.fullscreen_barcode)).check(matches(isDisplayed()))

        val viewById = rule.activity.findViewById(R.id.fullscreen_barcode) as ImageView
        val bitmapDrawable = viewById.drawable as BitmapDrawable
        val bitmap = bitmapDrawable.bitmap

        val bitmapToTest: Bitmap
        bitmapToTest = if (format === AZTEC) {
            // not sure why - but for the decoder to pick up AZTEC it must have moar pixelz - smells like a zxing bug
            bitmap.scale(bitmap.width * 2, bitmap.height * 2, filter = false)
        } else {
            bitmap
        }

        assertThat(bitmapToTest.decodeBarCode()).isEqualTo(BARCODE_MESSAGE)
    }

}
