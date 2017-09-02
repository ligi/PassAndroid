package org.ligi.passandroid.ui.edit

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import org.ligi.kaxt.loadImage
import org.ligi.passandroid.model.PassBitmapDefinitions
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import java.io.File
import java.io.IOException

class ImageEditHelper(private val context: Activity, private val passStore: PassStore) {

    fun startPick(reqCodePickLogo: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), reqCodePickLogo)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {

        if (resultCode == RESULT_OK && imageReturnedIntent != null) {
            val imageStringByRequestCode = getImageStringByRequestCode(requestCode)
            if (imageStringByRequestCode != null) {
                extractImage(imageReturnedIntent, imageStringByRequestCode)
            }
        }
    }

    private fun extractImage(imageReturnedIntent: Intent, name: String) {
        val extractedFile = imageReturnedIntent.data.loadImage(context)
        val pass = passStore.currentPass
        if (extractedFile != null && pass != null && extractedFile.exists()) {
            try {
                val destinationFile = File(passStore.getPathForID(pass.id), name + PassImpl.FILETYPE_IMAGES)
                destinationFile.delete()
                extractedFile.copyTo(destinationFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {

        private val REQ_CODE_OFFSET = 5555
        val REQ_CODE_PICK_LOGO = 1 + REQ_CODE_OFFSET
        val REQ_CODE_PICK_ICON = 2 + REQ_CODE_OFFSET
        val REQ_CODE_PICK_STRIP = 3 + REQ_CODE_OFFSET
        val REQ_CODE_PICK_THUMBNAIL = 4 + REQ_CODE_OFFSET
        val REQ_CODE_PICK_FOOTER = 5 + REQ_CODE_OFFSET

        @Pass.PassBitmap
        fun getImageStringByRequestCode(requestCode: Int): String? = when (requestCode) {
            REQ_CODE_PICK_LOGO -> PassBitmapDefinitions.BITMAP_LOGO
            REQ_CODE_PICK_ICON -> PassBitmapDefinitions.BITMAP_ICON
            REQ_CODE_PICK_THUMBNAIL -> PassBitmapDefinitions.BITMAP_THUMBNAIL
            REQ_CODE_PICK_STRIP -> PassBitmapDefinitions.BITMAP_STRIP
            REQ_CODE_PICK_FOOTER -> PassBitmapDefinitions.BITMAP_FOOTER
            else -> null
        }
    }


}
