package org.ligi.passandroid.ui.edit

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.ligi.kaxt.loadImage
import org.ligi.passandroid.model.PassBitmapDefinitions
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import java.io.File
import java.io.IOException

class ImageEditHelper(private val context: AppCompatActivity, private val passStore: PassStore) {

    private val pickVisualMedia: ActivityResultLauncher<PickVisualMediaRequest> = context.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val imageStringByRequestCode = reqCodePickLogo?.let { getImageStringByRequestCode(it) }
        if (imageStringByRequestCode != null) {
            extractImage(uri, imageStringByRequestCode)
        }
    }

    private var reqCodePickLogo: Int? = null

    fun startPick(reqCodePickLogo: Int) {
        this.reqCodePickLogo = reqCodePickLogo
        pickVisualMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun extractImage(imageReturned: Uri?, name: String) {
        val extractedFile = imageReturned?.loadImage(context)
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

        private const val REQ_CODE_OFFSET = 5555
        const val REQ_CODE_PICK_LOGO = 1 + REQ_CODE_OFFSET
        const val REQ_CODE_PICK_ICON = 2 + REQ_CODE_OFFSET
        const val REQ_CODE_PICK_STRIP = 3 + REQ_CODE_OFFSET
        const val REQ_CODE_PICK_THUMBNAIL = 4 + REQ_CODE_OFFSET
        const val REQ_CODE_PICK_FOOTER = 5 + REQ_CODE_OFFSET

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
