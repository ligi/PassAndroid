package org.ligi.passandroid.model.pass

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.squareup.moshi.JsonQualifier
import org.ligi.passandroid.model.PassStore
import org.threeten.bp.ZonedDateTime
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

class PassImpl(override val id: String) : Pass {


    @Retention(AnnotationRetention.RUNTIME)
    @JsonQualifier
    annotation class HexColor

    override var creator: String? = null

    override var type: PassType = PassType.EVENT

    override var barCode: BarCode? = null

    @field:[HexColor]
    override var accentColor: Int = 0

    override var description: String? = null
        get() {
            if (field == null) {
                return "" // better way of returning no description - so we can avoid optional / null checks and it is kind of the same thing
                // an navigation_drawer_header description - we can do kind of all String operations safely this way and do not have to care about the existence of a real description
                // if we want to know if one is there we can check length for being 0 still ( which we would have to do anyway for navigation_drawer_header descriptions )
                // See no way at the moment where we would have to distinguish between an navigation_drawer_header and an missing description
            }
            return field
        }

    class TimeRepeat(val offset: Int, val count: Int)
    class TimeSpan(val from: ZonedDateTime? = null, val to: ZonedDateTime? = null, val repeat: TimeRepeat? = null)

    override var validTimespans: List<TimeSpan> = ArrayList()

    override var calendarTimespan: TimeSpan? = null

    override var fields: MutableList<PassField> = ArrayList()

    override var locations: List<PassLocation> = ArrayList()

    override var app: String? = null

    override var authToken: String? = null

    override var webServiceURL: String? = null

    override var serial: String? = null

    override var passIdent: String? = null

    override fun getBitmap(passStore: PassStore, @Pass.PassBitmap passBitmap: String): Bitmap? {
        try {
            val file = File(passStore.getPathForID(id), passBitmap + FILETYPE_IMAGES)
            return BitmapFactory.decodeStream(FileInputStream(file))
        } catch (expectedInSomeCases_willJustReturnNull: FileNotFoundException) {
            return null
        } catch (e: OutOfMemoryError) {
            return null
        }

    }

    override fun getSource(passStore: PassStore): String? {
        val file = File(passStore.getPathForID(id), "source.txt")

        if (file.exists()) {
            return file.bufferedReader().readText()
        }
        return null
    }

    override fun toString(): String {
        return "ID=$id"
    }

    companion object {
        val FILETYPE_IMAGES = ".png"
    }

}
