package org.ligi.passandroid.reader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import org.json.JSONException
import org.json.JSONObject
import org.ligi.kaxt.parseColor
import org.ligi.passandroid.App
import org.ligi.passandroid.R
import org.ligi.passandroid.functions.getHumanCategoryString
import org.ligi.passandroid.functions.readJSONSafely
import org.ligi.passandroid.model.ApplePassbookQuirkCorrector
import org.ligi.passandroid.model.AppleStylePassTranslation
import org.ligi.passandroid.model.PassBitmapDefinitions
import org.ligi.passandroid.model.PassDefinitions
import org.ligi.passandroid.model.pass.*
import org.ligi.tracedroid.logging.Log
import org.threeten.bp.DateTimeException
import org.threeten.bp.ZonedDateTime
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.util.*

object AppleStylePassReader {

    fun read(passFile: File, language: String, context: Context): Pass? {

        val translation = AppleStylePassTranslation()

        val pass = PassImpl(passFile.name)

        var pass_json: JSONObject? = null

        val localized_path = findLocalizedPath(passFile, language)

        if (localized_path != null) {
            val file = File(localized_path, "pass.strings")
            translation.loadFromFile(file)
        }

        copyBitmapFile(passFile, localized_path, PassBitmapDefinitions.BITMAP_ICON)
        copyBitmapFile(passFile, localized_path, PassBitmapDefinitions.BITMAP_LOGO)
        copyBitmapFile(passFile, localized_path, PassBitmapDefinitions.BITMAP_STRIP)
        copyBitmapFile(passFile, localized_path, PassBitmapDefinitions.BITMAP_THUMBNAIL)
        copyBitmapFile(passFile, localized_path, PassBitmapDefinitions.BITMAP_FOOTER)

        val file = File(passFile, "pass.json")

        try {
            val plainJsonString = AppleStylePassTranslation.readFileAsStringGuessEncoding(file)
            pass_json = readJSONSafely(plainJsonString)
        } catch (e: Exception) {
            Log.i("PassParse Exception " + e)
        }

        if (pass_json == null) {
            // I had got a strange passbook with UCS-2 which could not be parsed before
            // was searching for a auto-detection, but could not find one with support for this encoding
            // and the right license

            for (charset in Charset.availableCharsets().values) {
                try {
                    val json_str = file.bufferedReader(charset).readText()
                    pass_json = readJSONSafely(json_str)
                } catch (ignored: Exception) {
                    // we try with next charset
                }

                if (pass_json != null) {
                    break
                }
            }
        }

        if (pass_json == null) {
            Log.w("could not load pass.json from passcode ")
            App.tracker.trackEvent("problem_event", "pass", "without_pass_json", null)
            return null
        }

        try {
            val barcode_json = pass_json.getBarcodeJson()
            if (barcode_json != null) {
                val barcodeFormatString = barcode_json.getString("format")

                App.tracker.trackEvent("measure_event", "barcode_format", barcodeFormatString, 0L)
                val barcodeFormat = BarCode.getFormatFromString(barcodeFormatString)
                val barCode = BarCode(barcodeFormat, barcode_json.getString("message"))
                pass.barCode = barCode

                if (barcode_json.has("altText")) {
                    pass.barCode!!.alternativeText = barcode_json.getString("altText")
                }
            }
            // TODO should check a bit more with barcode here - this can be dangerous
        } catch (ignored: Exception) {
        }

        if (pass_json.has("relevantDate")) {
            try {
                pass.calendarTimespan = PassImpl.TimeSpan(from = ZonedDateTime.parse(pass_json.getString("relevantDate")))
            } catch (e: JSONException) {
                // be robust when it comes to bad dates - had a RL crash with "2013-12-25T00:00-57:00" here
                // OK then we just have no date here
                App.tracker.trackException("problem parsing relevant date", e, false)
            } catch (e: DateTimeException) {
                App.tracker.trackException("problem parsing relevant date", e, false)
            }

        }

        if (pass_json.has("expirationDate")) {
            try {
                pass.validTimespans = listOf(PassImpl.TimeSpan(to = ZonedDateTime.parse(pass_json.getString("expirationDate"))))
            } catch (e: JSONException) {
                // be robust when it comes to bad dates - had a RL crash with "2013-12-25T00:00-57:00" here
                // OK then we just have no date here
                App.tracker.trackException("problem parsing expiration date", e, false)
            } catch (e: DateTimeException) {
                App.tracker.trackException("problem parsing expiration date", e, false)
            }

        }

        pass.serial = readJsonSafeAsOptional(pass_json, "serialNumber")
        pass.authToken = readJsonSafeAsOptional(pass_json, "authenticationToken")
        pass.webServiceURL = readJsonSafeAsOptional(pass_json, "webServiceURL")
        pass.passIdent = readJsonSafeAsOptional(pass_json, "passTypeIdentifier")

        val locations = ArrayList<PassLocation>()
        try {

            val locations_json = pass_json.getJSONArray("locations")
            for (i in 0..locations_json.length() - 1) {
                val obj = locations_json.getJSONObject(i)

                val location = PassLocation()
                location.lat = obj.getDouble("latitude")
                location.lon = obj.getDouble("longitude")

                if (obj.has("relevantText")) {
                    location.name = translation.translate(obj.getString("relevantText"))
                }

                locations.add(location)
            }

        } catch (ignored: JSONException) {
        }

        pass.locations = locations

        readJsonSafe(pass_json, "backgroundColor", object : JsonStringReadCallback {
            override fun onString(string: String) {
                pass.accentColor = string.parseColor(Color.BLACK)
            }
        })

        readJsonSafe(pass_json, "description", object : JsonStringReadCallback {
            override fun onString(string: String) {
                pass.description = translation.translate(string)
            }
        })


        // try to find in a predefined set of tickets

        PassDefinitions.TYPE_TO_NAME.forEach {
            if (pass_json!!.has(it.value)) {
                pass.type = it.key
            }
        }

        try {
            val type = PassDefinitions.TYPE_TO_NAME[pass.type]
            val type_json = pass_json.getJSONObject(type)
            if (type_json != null) {
                val fieldList: ArrayList<PassField> = ArrayList()

                addFields(fieldList, type_json, "primaryFields", translation)
                addFields(fieldList, type_json, "headerFields", translation)
                addFields(fieldList, type_json, "secondaryFields", translation)
                addFields(fieldList, type_json, "auxiliaryFields", translation)
                addFields(fieldList, type_json, "backFields", translation, hide = true)

                fieldList.add(PassField("", context.getString(R.string.type), context.getString(getHumanCategoryString(pass.type)), false))
                pass.fields = fieldList
            }

        } catch (ignored: JSONException) {
        }


        try {
            pass.creator = pass_json.getString("organizationName")
            App.tracker.trackEvent("measure_event", "organisation_parse", pass.creator, 1L)
        } catch (ignored: JSONException) {
            // ok - we have no organisation - big deal ..-)
        }

        ApplePassbookQuirkCorrector(App.tracker).correctQuirks(pass)

        return pass
    }

    private fun getField(jsonObject: JSONObject, key: String, translation: AppleStylePassTranslation): String? {
        if (jsonObject.has(key)) {
            try {
                return translation.translate(jsonObject.getString(key))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun addFields(list: ArrayList<PassField>, type_json: JSONObject, fieldsName: String, translation: AppleStylePassTranslation, hide: Boolean = false) {
        try {
            val jsonArray = type_json.getJSONArray(fieldsName)
            for (i in 0..jsonArray.length() - 1) {
                try {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val field = PassField(key = getField(jsonObject, "key", translation),
                            label = getField(jsonObject, "label", translation),
                            value = getField(jsonObject, "value", translation),
                            hide = hide)
                    list.add(field)

                } catch (e: JSONException) {
                    Log.w("could not process PassField from JSON for $fieldsName cause:$e")
                }

            }
        } catch (e: JSONException) {
            Log.w("could not process PassFields $fieldsName from JSON$e")
        }

    }

    private fun findLocalizedPath(path: File, language: String): String? {

        val localized = File(path, language + ".lproj")

        if (localized.exists() && localized.isDirectory) {
            App.tracker.trackEvent("measure_event", "pass", language + "_native_lproj", null)
            return localized.path
        }

        val fallback = File(path, "en.lproj")

        if (fallback.exists() && fallback.isDirectory) {
            App.tracker.trackEvent("measure_event", "pass", "en_lproj", null)
            return fallback.path
        }

        return null
    }

    internal interface JsonStringReadCallback {
        fun onString(string: String)
    }

    private fun readJsonSafeAsOptional(json: JSONObject, key: String): String? {
        if (json.has(key)) {
            try {
                return json.getString(key)
            } catch (e: JSONException) {
                // some passes just do not have the field
            }

        }
        return null
    }

    private fun readJsonSafe(json: JSONObject, key: String, callback: JsonStringReadCallback) {
        if (json.has(key)) {
            try {
                callback.onString(json.getString(key))
            } catch (e: JSONException) {
                // some passes just do not have the field
            }

        }
    }

    private fun copyBitmapFile(path: File, localizedPath: String?, bitmapString: String) {
        val bitmap = findBitmap(path, localizedPath, bitmapString)
        if (bitmap != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(File(path, bitmapString + PassImpl.FILETYPE_IMAGES)))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun findBitmap(path: File, localizedPath: String?, bitmap: String): Bitmap? {

        val searchList = ArrayList<File>()
        if (localizedPath != null) {
            searchList.add(File(localizedPath, "$bitmap@2x.png"))
            searchList.add(File(localizedPath, "$bitmap.png"))
        }

        searchList.add((File(path, "$bitmap@2x.png")))
        searchList.add((File(path, "$bitmap@2x.png")))

        for (current in searchList) {

            var res: Bitmap? = null

            try {
                res = BitmapFactory.decodeFile(current.absolutePath)
            } catch (e: OutOfMemoryError) {
                System.gc()
                try {
                    res = BitmapFactory.decodeFile(current.absolutePath)
                } catch (e: OutOfMemoryError) {
                }
            }

            if (res != null) {
                return res
            }
        }
        return null
    }


}
