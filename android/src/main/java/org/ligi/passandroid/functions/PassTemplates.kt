package org.ligi.passandroid.functions

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.ligi.passandroid.R
import org.ligi.passandroid.model.PassBitmapDefinitions
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassField
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.model.pass.PassType
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*

val APP = "passandroid"

fun createAndAddEmptyPass(passStore: PassStore, resources: Resources): Pass {
    val pass = createBasePass()

    pass.description = "custom Pass"

    passStore.currentPass = pass
    passStore.save(pass)

    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)

    try {
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, FileOutputStream(File(passStore.getPathForID(pass.id), PassBitmapDefinitions.BITMAP_ICON + ".png")))
    } catch (ignored: FileNotFoundException) {
    }

    return pass
}

fun createPassForImageImport(resources: Resources): Pass {
    return createBasePass().apply {
        description = resources.getString(R.string.image_import)

        fields = mutableListOf(
                PassField.create(R.string.field_source, R.string.field_source_image, resources),
                PassField.create(R.string.field_advice_label, R.string.field_advice_text, resources),
                PassField.create(R.string.field_note, R.string.field_note_image, resources, true)
        )
    }
}

fun createPassForPDFImport(resources: Resources): Pass {
    return createBasePass().apply {
        description = resources.getString(R.string.pdf_import)

        fields = mutableListOf(
                PassField.create(R.string.field_source, R.string.field_source_pdf, resources),
                PassField.create(R.string.field_advice_label, R.string.field_advice_text, resources),
                PassField.create(R.string.field_note, R.string.field_note_pdf, resources, true)
        )
    }
}

private fun createBasePass(): PassImpl {
    val pass = PassImpl(UUID.randomUUID().toString())
    pass.accentColor = 0xFF0000FF.toInt()
    pass.app = APP
    pass.type = PassType.EVENT
    return pass
}
