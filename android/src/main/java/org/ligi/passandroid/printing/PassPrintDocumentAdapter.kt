package org.ligi.passandroid.printing

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import org.ligi.passandroid.model.pass.Pass
import java.io.FileOutputStream
import java.io.IOException

@TargetApi(Build.VERSION_CODES.KITKAT)
class PassPrintDocumentAdapter(private val context: Context, private val pass: Pass, private val jobName: String) : PrintDocumentAdapter() {

    private var mPdfDocument: PrintedPdfDocument? = null

    override fun onLayout(oldAttributes: PrintAttributes,
                          newAttributes: PrintAttributes,
                          cancellationSignal: CancellationSignal,
                          layoutResultCallback: PrintDocumentAdapter.LayoutResultCallback,
                          bundle: Bundle) {

        if (cancellationSignal.isCanceled) {
            layoutResultCallback.onLayoutCancelled()
            return
        }

        mPdfDocument = PrintedPdfDocument(context, newAttributes)

        val info = PrintDocumentInfo.Builder(jobName).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(1).build()
        layoutResultCallback.onLayoutFinished(info, true)

    }

    override fun onWrite(pageRanges: Array<PageRange>,
                         destination: ParcelFileDescriptor,
                         cancellationSignal: CancellationSignal,
                         callback: PrintDocumentAdapter.WriteResultCallback) {

        val page = mPdfDocument!!.startPage(0)
        val canvas = page.canvas

        drawPass(canvas)
        mPdfDocument!!.finishPage(page)

        try {
            mPdfDocument!!.writeTo(FileOutputStream(destination.fileDescriptor))
        } catch (e: IOException) {
            callback.onWriteFailed(e.toString())
            return
        } finally {
            mPdfDocument!!.close()
            mPdfDocument = null
        }

        callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }

    private fun drawPass(canvas: Canvas) {
        val centerPaint = Paint()
        centerPaint.textAlign = Paint.Align.CENTER

        canvas.drawText(pass.description, canvas.width / 2f, centerPaint.textSize, centerPaint)
        var currentBottom = centerPaint.textSize * 3

        val barCode = pass.barCode
        if (barCode != null) {
            val bitmapDrawable = barCode.getBitmap(context.resources)

            if (bitmapDrawable != null) {
                val bitmap = bitmapDrawable.bitmap
                val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
                val ratio = (canvas.width / 3f) / bitmap.width

                val destRect = Rect(0, 0, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt())

                destRect.offset(((canvas.width - destRect.width()) / 2).toInt(), currentBottom.toInt())

                currentBottom += destRect.bottom

                canvas.drawBitmap(bitmap, srcRect, destRect, centerPaint)

                if (barCode.alternativeText != null) {
                    canvas.drawText(barCode.alternativeText, destRect.centerX().toFloat(), destRect.bottom.toFloat() + 7 + centerPaint.textSize, centerPaint)
                    currentBottom += 7 + centerPaint.textSize * 2
                }
            }
        }

        val leftPaint = Paint()
        leftPaint.textAlign = Paint.Align.RIGHT
        leftPaint.isFakeBoldText = true

        val rightPaint = Paint()
        rightPaint.textAlign = Paint.Align.LEFT

        pass.fields.forEach {
            if (!it.hide) {
                canvas.drawText(it.label + ": ", canvas.width / 2f, currentBottom, leftPaint)
                canvas.drawText(" " + it.value, canvas.width / 2f, currentBottom, rightPaint)
                currentBottom += (centerPaint.textSize * 1.5).toInt()
            }
        }
    }
}
