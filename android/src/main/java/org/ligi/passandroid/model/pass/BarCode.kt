package org.ligi.passandroid.model.pass

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import com.squareup.moshi.JsonClass
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ligi.passandroid.Tracker
import org.ligi.passandroid.functions.generateBitmapDrawable
import timber.log.Timber
import java.util.*

@JsonClass(generateAdapter = true)
class BarCode(val format: PassBarCodeFormat?, val message: String? = UUID.randomUUID().toString().toUpperCase()) : KoinComponent {

    val tracker: Tracker by inject ()
    var alternativeText: String? = null

    fun getBitmap(resources: Resources): BitmapDrawable? {
        if (message == null) {
            // no message -> no barcode
            tracker.trackException("No Barcode in pass - strange", false)
            return null
        }

        if (format == null) {
            Timber.w("Barcode format is null - fallback to QR")
            tracker.trackException("Barcode format is null - fallback to QR", false)
            return generateBitmapDrawable(resources, message, PassBarCodeFormat.QR_CODE)
        }

        return generateBitmapDrawable(resources, message, format)

    }

    companion object {

        fun getFormatFromString(format: String): PassBarCodeFormat {
            return when {
                format.contains("417") -> PassBarCodeFormat.PDF_417
                format.toUpperCase(Locale.ENGLISH).contains("AZTEC") -> return PassBarCodeFormat.AZTEC
                format.toUpperCase(Locale.ENGLISH).contains("128") -> return PassBarCodeFormat.CODE_128
                format.toUpperCase(Locale.ENGLISH).contains("39") -> return PassBarCodeFormat.CODE_39

                 /*
                 requested but not supported by xing (yet)   https://github.com/ligi/PassAndroid/issues/43
                 format.toUpperCase(Locale.ENGLISH).contains("93")->return BarcodeFormat.CODE_93;
                 */

                else -> PassBarCodeFormat.QR_CODE

            }


        }
    }

}
