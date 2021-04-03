package org.ligi.passandroid.reader

import android.graphics.Color
import org.ligi.passandroid.functions.readJSONSafely
import org.ligi.passandroid.model.PassDefinitions
import org.ligi.passandroid.model.pass.BarCode
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.model.pass.PassType
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import java.io.File

object PassReader {

    fun read(path: File): Pass {

        val pass = PassImpl(path.name)

        val file = File(path, "data.json")

        try {
            val plainJsonString = file.bufferedReader().readText()
            val passJSON = readJSONSafely(plainJsonString)!!

            if (passJSON.has("what")) {
                val whatJSON = passJSON.getJSONObject("what")
                pass.description = whatJSON.getString("description")
            }

            if (passJSON.has("meta")) {
                val metaJSON = passJSON.getJSONObject("meta")
                pass.type = PassDefinitions.NAME_TO_TYPE[metaJSON.getString("type")] ?: PassType.GENERIC
                pass.creator = metaJSON.getString("organisation")
                pass.app = metaJSON.getString("app")
            }

            if (passJSON.has("ui")) {
                val uiJSON = passJSON.getJSONObject("ui")
                pass.accentColor = Color.parseColor(uiJSON.getString("bgColor"))
            }

            if (passJSON.has("barcode")) {
                val barcodeJSON = passJSON.getJSONObject("barcode")
                val barcodeFormatString = barcodeJSON.getString("type")

                val barcodeFormat = BarCode.getFormatFromString(barcodeFormatString)
                val barCode = BarCode(barcodeFormat, barcodeJSON.getString("message"))
                pass.barCode = barCode

                if (barcodeJSON.has("altText")) {
                    barCode.alternativeText = barcodeJSON.getString("altText")
                }
            }

            if (passJSON.has("when")) {
                val dateTime = passJSON.getJSONObject("when").getString("dateTime")

                pass.calendarTimespan = PassImpl.TimeSpan()
                pass.calendarTimespan = PassImpl.TimeSpan(from = ZonedDateTime.parse(dateTime))
            }

        } catch (e: Exception) {
            Timber.i("PassParse Exception: $e")
        }

        return pass
    }

}
