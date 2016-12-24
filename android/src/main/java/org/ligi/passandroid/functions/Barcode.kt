package org.ligi.passandroid.functions

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.google.zxing.MultiFormatWriter
import org.ligi.passandroid.model.pass.PassBarCodeFormat
import org.ligi.tracedroid.logging.Log


fun generateBitmapDrawable(resources: Resources, data: String, type: PassBarCodeFormat): BitmapDrawable? {
    val bitmap = generateBarCodeBitmap(data, type) ?: return null

    return BitmapDrawable(resources, bitmap).apply {
        isFilterBitmap = false
        setAntiAlias(false)
    }
}

fun generateBarCodeBitmap(data: String, type: PassBarCodeFormat): Bitmap? {

    if (data.isEmpty()) {
        return null
    }

    try {
        val matrix = getBitMatrix(data, type)
        val is1D = matrix.height == 1

        // generate an image from the byte matrix
        val width = matrix.width
        val height = if (is1D) width / 5 else matrix.height

        // create buffered image to draw to
        // NTFS Bitmap.Config.ALPHA_8 sounds like an awesome idea - been there - done that ..
        val barcode_image = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        // iterate through the matrix and draw the pixels to the image
        for (y in 0..height - 1) {
            for (x in 0..width - 1) {
                barcode_image.setPixel(x, y, if (matrix.get(x, if (is1D) 0 else y)) 0 else 0xFFFFFF)
            }
        }

        return barcode_image
    } catch (e: com.google.zxing.WriterException) {
        Log.w("could not write image " + e)
        // TODO check if we should better return some rescue Image here
        return null
    } catch (e: IllegalArgumentException) {
        Log.w("could not write image " + e)
        return null
    } catch (e: ArrayIndexOutOfBoundsException) {
        // happens for ITF barcode on certain inputs
        Log.w("could not write image " + e)
        return null
    }

}

fun getBitMatrix(data: String, type: PassBarCodeFormat)
        = MultiFormatWriter().encode(data, type.zxingBarCodeFormat(), 0, 0)!!

