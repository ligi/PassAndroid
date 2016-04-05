package org.ligi.passandroid.model.pass

import android.graphics.Bitmap
import android.support.annotation.StringDef
import org.ligi.passandroid.model.PassBitmapDefinitions.*
import org.ligi.passandroid.model.PassStore


interface Pass {

    @StringDef(BITMAP_ICON, BITMAP_THUMBNAIL, BITMAP_STRIP, BITMAP_LOGO, BITMAP_FOOTER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class PassBitmap

    val description: String?

    val type: PassType

    val fields: List<PassField>

    val locations: List<PassLocation>

    val accentColor: Int

    val id: String

    val creator: String?

    fun getSource(passStore: PassStore): String?

    val barCode: BarCode?

    val webServiceURL: String?

    val authToken: String?

    val serial: String?

    val passIdent: String?

    val app: String?

    val validTimespans: List<PassImpl.TimeSpan>
    val calendarTimespan: PassImpl.TimeSpan?

    fun getBitmap(passStore: PassStore, passBitmap: String): Bitmap?

}
