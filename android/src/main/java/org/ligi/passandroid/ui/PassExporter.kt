package org.ligi.passandroid.ui

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants

import org.ligi.passandroid.App

import java.io.File

class PassExporter(private val inputPath: File, val file: File) {
    var exception: Exception? = null

    fun export() {
        try {
            file.delete()
            file.parentFile.mkdirs()
            val zipFile = ZipFile(file)

            zipFile.createZipFileFromFolder(inputPath, object : ZipParameters() {
                init {
                    isIncludeRootFolder = false
                    compressionMethod = Zip4jConstants.COMP_DEFLATE
                    compressionLevel = Zip4jConstants.DEFLATE_LEVEL_NORMAL
                }
            }, false, 0)

        } catch (exception: Exception) {
            exception.printStackTrace()
            App.tracker.trackException("when exporting pass to zip", exception, false)
            this.exception = exception // we need to take action on the main thread later
            file.delete() // prevent zombies from taking over
        }

    }
}
