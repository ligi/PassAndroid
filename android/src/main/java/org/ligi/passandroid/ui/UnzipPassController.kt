package org.ligi.passandroid.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import okio.Okio
import org.json.JSONObject
import org.ligi.passandroid.App
import org.ligi.passandroid.functions.createPassForImageImport
import org.ligi.passandroid.functions.createPassForPDFImport
import org.ligi.passandroid.functions.readJSONSafely
import org.ligi.passandroid.model.InputStreamWithSource
import org.ligi.passandroid.model.PassStore
import org.ligi.tracedroid.logging.Log
import java.io.File
import java.io.FileOutputStream
import java.util.*

object UnzipPassController {

    interface SuccessCallback {
        fun call(uuid: String)
    }

    interface FailCallback {
        fun fail(reason: String)
    }

    fun processInputStream(spec: InputStreamUnzipControllerSpec) {
        try {
            val tempFile = File.createTempFile("ins", "pass")
            spec.inputStreamWithSource.inputStream.copyTo(FileOutputStream(tempFile))
            processFile(FileUnzipControllerSpec(tempFile.absolutePath, spec))
            tempFile.delete()
        } catch (e: Exception) {
            App.tracker.trackException("problem processing InputStream", e, false)
            spec.failCallback?.fail("problem with temp file" + e)
        }

    }

    private fun processFile(spec: FileUnzipControllerSpec) {

        var uuid = UUID.randomUUID().toString()
        val path = File(spec.context.cacheDir, "temp/" + uuid)

        path.mkdirs()

        if (!path.exists()) {
            spec.failCallback?.fail("Problem creating the temp dir: " + path)
            return
        }

        File(path, "source.obj").bufferedWriter().write(spec.source)

        try {
            val zipFile = ZipFile(spec.zipFileString)
            zipFile.extractAll(path.absolutePath)
        } catch (e: ZipException) {
            e.printStackTrace()
        }


        val manifest_json: JSONObject
        val manifestFile = File(path, "manifest.json")
        val espassFile = File(path, "main.json")

        if (manifestFile.exists()) {
            try {
                val readToString = manifestFile.bufferedReader().readText()
                manifest_json = readJSONSafely(readToString)!!
                uuid = manifest_json.getString("pass.json")
            } catch (e: Exception) {
                spec.failCallback?.fail("Problem with manifest.json: " + e)
                return
            }

        } else if (espassFile.exists()) {
            try {
                val readToString = espassFile.bufferedReader().readText()
                manifest_json = readJSONSafely(readToString)!!
                uuid = manifest_json.getString("id")
            } catch (e: Exception) {
                spec.failCallback?.fail("Problem with manifest.json: " + e)
                return
            }

        } else {


            val bitmap = BitmapFactory.decodeFile(spec.zipFileString)

            val resources = spec.context.resources
            if (bitmap != null) {
                val imagePass = createPassForImageImport(resources)
                val pathForID = spec.passStore.getPathForID(imagePass.id)
                pathForID.mkdirs()

                File(spec.zipFileString).copyTo(File(pathForID, "strip.png"))

                spec.passStore.save(imagePass)
                spec.passStore.classifier.moveToTopic(imagePass, "new")
                spec.onSuccessCallback?.call(imagePass.id)
                return
            }

            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    val file = File(spec.zipFileString)
                    val readUtf8 = Okio.buffer(Okio.source(file)).readUtf8(4)
                    if (readUtf8 == "%PDF") {
                        val open = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                        val pdfRenderer = PdfRenderer(open)

                        val page = pdfRenderer.openPage(0)
                        val ratio = page.height.toFloat() / page.width

                        val widthPixels = resources.displayMetrics.widthPixels
                        val createBitmap = Bitmap.createBitmap(widthPixels, (widthPixels * ratio).toInt(), Bitmap.Config.ARGB_8888)
                        page.render(createBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                        val imagePass = createPassForPDFImport(resources)
                        val pathForID = spec.passStore.getPathForID(imagePass.id)
                        pathForID.mkdirs()

                        createBitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(File(pathForID, "strip.png")))

                        spec.passStore.save(imagePass)
                        spec.passStore.classifier.moveToTopic(imagePass, "new")
                        spec.onSuccessCallback?.call(imagePass.id)
                        return
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            spec.failCallback?.fail("Pass is not espass or pkpass format :-(")
            return
        }

        spec.targetPath.mkdirs()
        val rename_file = File(spec.targetPath, uuid)

        if (spec.overwrite && rename_file.exists()) {
            rename_file.deleteRecursively()
        }

        if (!rename_file.exists()) {
            path.renameTo(rename_file)
        } else {
            Log.i("Pass with same ID exists")
        }

        spec.onSuccessCallback?.call(uuid)
    }

    class InputStreamUnzipControllerSpec(internal val inputStreamWithSource: InputStreamWithSource, context: Context, passStore: PassStore,
                                         onSuccessCallback: SuccessCallback?, failCallback: FailCallback?) : UnzipControllerSpec(context, passStore, onSuccessCallback, failCallback)

}
