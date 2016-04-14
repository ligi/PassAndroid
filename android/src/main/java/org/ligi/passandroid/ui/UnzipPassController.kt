package org.ligi.passandroid.ui

import android.content.Context
import android.graphics.BitmapFactory
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import org.json.JSONObject
import org.ligi.axt.AXT
import org.ligi.passandroid.App
import org.ligi.passandroid.helper.PassTemplates
import org.ligi.passandroid.helper.SafeJSONReader
import org.ligi.passandroid.model.InputStreamWithSource
import org.ligi.passandroid.model.PassStore
import org.ligi.tracedroid.logging.Log
import java.io.File
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
            AXT.at(spec.inputStreamWithSource.inputStream).toFile(tempFile)
            processFile(FileUnzipControllerSpec(tempFile.absolutePath, spec))
            tempFile.delete()
        } catch (e: Exception) {
            App.component().tracker().trackException("problem processing InputStream", e, false)
            spec.failCallback.fail("problem with temp file" + e)
        }

    }

    private fun processFile(spec: FileUnzipControllerSpec) {

        var uuid = UUID.randomUUID().toString()
        val path = File(spec.context.cacheDir, "temp/" + uuid)

        path.mkdirs()

        if (!path.exists()) {
            spec.failCallback.fail("Problem creating the temp dir: " + path)
            return
        }

        AXT.at(File(path, "source.obj")).writeObject(spec.source)

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
                val readToString = AXT.at(manifestFile).readToString()
                manifest_json = SafeJSONReader.readJSONSafely(readToString)
                uuid = manifest_json.getString("pass.json")
            } catch (e: Exception) {
                spec.failCallback.fail("Problem with manifest.json: " + e)
                return
            }

        } else if (espassFile.exists()) {
            try {
                val readToString = AXT.at(espassFile).readToString()
                manifest_json = SafeJSONReader.readJSONSafely(readToString)
                uuid = manifest_json.getString("id")
            } catch (e: Exception) {
                spec.failCallback.fail("Problem with manifest.json: " + e)
                return
            }

        } else {


            val bitmap = BitmapFactory.decodeFile(spec.zipFileString)

            if (bitmap != null) {
                val imagePass = PassTemplates.createPassForImageImport();
                val pathForID = spec.passStore.getPathForID(imagePass.id)
                pathForID.mkdir()

                File(spec.zipFileString).copyTo(File(pathForID,"strip.png"))

                spec.passStore.save(imagePass)
                spec.passStore.classifier.moveToTopic(imagePass,"new")
                spec.onSuccessCallback.call(imagePass.id)
            } else {
                spec.failCallback.fail("Pass is not espass or pkpass format :-(")
            }
            return
        }

        spec.targetPath.mkdirs()
        val rename_file = File(spec.targetPath,uuid)

        if (spec.overwrite && rename_file.exists()) {
            AXT.at(rename_file).deleteRecursive()
        }

        if (!rename_file.exists()) {
            path.renameTo(rename_file)
        } else {
            Log.i("Pass with same ID exists")
        }

        spec.onSuccessCallback.call(uuid)
    }

    class InputStreamUnzipControllerSpec(internal val inputStreamWithSource: InputStreamWithSource, context: Context, passStore: PassStore,
                                         onSuccessCallback: SuccessCallback, failCallback: FailCallback) : UnzipControllerSpec(context, passStore, onSuccessCallback, failCallback)

}
