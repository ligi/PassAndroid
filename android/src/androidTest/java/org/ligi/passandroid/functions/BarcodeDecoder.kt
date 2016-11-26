package org.ligi.passandroid.functions

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

fun Bitmap.decodeBarCode(): String {
    val intArray = IntArray(this.width * this.height)
    this.getPixels(intArray, 0, this.width, 0, 0, this.width, this.height)

    val source = RGBLuminanceSource(this.width, this.height, intArray)
    val bitmap = BinaryBitmap(HybridBinarizer(source))

    val reader = MultiFormatReader()// use this otherwise ChecksumException

    val result = reader.decode(bitmap)
    return result.text
}


