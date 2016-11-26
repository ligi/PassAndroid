package org.ligi.passandroid.reader

import android.graphics.Color
import org.ligi.passandroid.functions.readJSONSafely
import org.ligi.passandroid.model.PassDefinitions
import org.ligi.passandroid.model.pass.BarCode
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.model.pass.PassType
import org.ligi.tracedroid.logging.Log
import org.threeten.bp.ZonedDateTime
import java.io.File

object PassReader {

    fun read(path: File): Pass {

        val pass = PassImpl(path.name)

        val file = File(path, "data.json")

        try {
            val plainJsonString = file.bufferedReader().readText()
            val pass_json = readJSONSafely(plainJsonString)!!

            if (pass_json.has("what")) {
                val what_json = pass_json.getJSONObject("what")
                pass.description = what_json.getString("description")
            }

            if (pass_json.has("meta")) {
                val meta_json = pass_json.getJSONObject("meta")
                pass.type = PassDefinitions.NAME_TO_TYPE[meta_json.getString("type")] ?: PassType.GENERIC
                pass.creator = meta_json.getString("organisation")
                pass.app = meta_json.getString("app")
            }

            if (pass_json.has("ui")) {
                val ui_json = pass_json.getJSONObject("ui")
                pass.accentColor = Color.parseColor(ui_json.getString("bgColor"))
            }

            if (pass_json.has("barcode")) {
                val barcode_json = pass_json.getJSONObject("barcode")
                val barcodeFormatString = barcode_json.getString("type")

                val barcodeFormat = BarCode.getFormatFromString(barcodeFormatString)
                val barCode = BarCode(barcodeFormat, barcode_json.getString("message"))
                pass.barCode = barCode

                if (barcode_json.has("altText")) {
                    barCode.alternativeText = barcode_json.getString("altText")
                }
            }

            if (pass_json.has("when")) {
                val dateTime = pass_json.getJSONObject("when").getString("dateTime")

                pass.calendarTimespan = PassImpl.TimeSpan()
                pass.calendarTimespan = PassImpl.TimeSpan(from = ZonedDateTime.parse(dateTime))
            }

        } catch (e: Exception) {
            Log.i("PassParse Exception " + e)
        }

        return pass
    }

}
