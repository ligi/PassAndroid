package org.ligi.passandroid.model

import android.content.Context
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import okio.buffer
import okio.sink
import okio.source
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ligi.passandroid.BuildConfig
import org.ligi.passandroid.Tracker
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.reader.AppleStylePassReader
import org.ligi.passandroid.reader.PassReader
import java.io.File
import java.util.*

object PassStoreUpdateEvent

class AndroidFileSystemPassStore(
        private val context: Context,
        settings: Settings,
        private val moshi: Moshi
) : PassStore, KoinComponent {

    override val updateChannel = ConflatedBroadcastChannel<PassStoreUpdateEvent>()

    private val path: File = settings.getPassesDir()

    override val passMap = HashMap<String, Pass>()

    override var currentPass: Pass? = null

    private val tracker: Tracker by inject()

    override val classifier: PassClassifier by lazy {
        val classificationFile = File(settings.getStateDir(), "classifier_state.json")
        FileBackedPassClassifier(classificationFile, this, moshi)
    }

    override fun save(pass: Pass) {
        val jsonAdapter = moshi.adapter(PassImpl::class.java)

        val pathForID = getPathForID(pass.id)

        if (!pathForID.exists()) {
            pathForID.mkdirs()
        }

        val buffer = File(pathForID, "main.json").sink().buffer()

        if (BuildConfig.DEBUG) {
            val of = com.squareup.moshi.JsonWriter.of(buffer)
            of.indent = "  "
            jsonAdapter.toJson(of, pass as PassImpl)
            buffer.close()
            of.close()
        } else {
            jsonAdapter.toJson(buffer, pass as PassImpl)
            buffer.close()
        }

        passMap[pass.id] = pass
    }

    private fun readPass(id: String): Pass? {
        val pathForID = getPathForID(id)
        val language = context.resources.configuration.locale.language

        if (!pathForID.exists() || !pathForID.isDirectory) {
            return null
        }

        val file = File(pathForID, "main.json")
        var result: Pass? = null
        var dirty = true
        if (file.exists()) {
            val jsonAdapter = moshi.adapter(PassImpl::class.java)
            dirty = false
            try {
                result = jsonAdapter.fromJson(file.source().buffer())
            } catch (ignored: JsonDataException) {
                tracker.trackException("invalid main.json", false)
            }
        }

        if (result == null && File(pathForID, "data.json").exists()) {
            result = PassReader.read(pathForID)
            File(pathForID, "data.json").delete()
        }

        if (result == null && File(pathForID, "pass.json").exists()) {
            result = AppleStylePassReader.read(pathForID, language, context, tracker)
        }

        if (result != null) {
            if (dirty) {
                save(result)
            }
            passMap[id] = result
            notifyChange()
        }

        return result
    }

    override fun getPassbookForId(id: String): Pass? {
        return passMap[id] ?: readPass(id)
    }

    override fun deletePassWithId(id: String): Boolean {
        val result = getPathForID(id).deleteRecursively()
        if (result) {
            passMap.remove(id)
            classifier.removePass(id)
            notifyChange()
        }
        return result
    }

    override fun getPathForID(id: String): File {
        return File(path, id)
    }

    override fun notifyChange() {
        GlobalScope.launch {
            updateChannel.send(PassStoreUpdateEvent)
        }
    }

    override fun syncPassStoreWithClassifier(defaultTopic: String) {
        val keysToRemove = classifier.topicByIdMap.keys.filter { getPassbookForId(it) == null }

        for (key in keysToRemove) {
            classifier.topicByIdMap.remove(key)
        }

        val allPasses = path.listFiles()
        allPasses?.forEach {
            classifier.getTopic(it.name, defaultTopic)
        }
    }
}
