package org.ligi.passandroid.ui

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ligi.passandroid.Tracker
import java.io.File

class PassExporter(private val inputPath: File, val file: File) : KoinComponent {
    var exception: Exception? = null

    val tracker: Tracker by inject()

    fun export() {
        try {
            file.delete()
            file.parentFile.mkdirs()
            val zipFile = ZipFile(file)

            zipFile.createSplitZipFileFromFolder(inputPath, object : ZipParameters() {
                init {
                    isIncludeRootFolder = false
                    compressionMethod = CompressionMethod.DEFLATE
                    compressionLevel = CompressionLevel.NORMAL
                }
            }, false, 0)

        } catch (exception: Exception) {
            exception.printStackTrace()
            tracker.trackException("when exporting pass to zip", exception, false)
            this.exception = exception // we need to take action on the main thread later
            file.delete() // prevent zombies from taking over
        }

    }
}
