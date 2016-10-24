package org.ligi.passandroid.helper

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

object PassTemplates {

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

    fun createPassForImageImport(): Pass {
        return createBasePass().apply {
            description = "image import"

            fields = mutableListOf(
                    PassField(null, "source", "image", false),
                    PassField(null, "note", "This is imported from image - not a real pass", false)
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
}
