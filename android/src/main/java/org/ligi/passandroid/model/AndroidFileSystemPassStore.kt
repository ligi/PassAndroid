package org.ligi.passandroid.model

import android.content.Context
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import okio.Okio
import org.greenrobot.eventbus.EventBus
import org.ligi.passandroid.App
import org.ligi.passandroid.BuildConfig
import org.ligi.passandroid.events.PassStoreChangeEvent
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.reader.AppleStylePassReader
import org.ligi.passandroid.reader.PassReader
import java.io.File
import java.util.*

class AndroidFileSystemPassStore(private val context: Context, settings: Settings, private val moshi: Moshi, private val bus: EventBus) : PassStore {
    private val path: File = settings.getPassesDir()

    override val passMap = HashMap<String, Pass>()

    override var currentPass: Pass? = null

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

        val buffer = Okio.buffer(Okio.sink(File(pathForID, "main.json")))

        if (BuildConfig.DEBUG) {
            val of = com.squareup.moshi.JsonWriter.of(buffer)
            of.setIndent("  ")
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
                result = jsonAdapter.fromJson(Okio.buffer(Okio.source(file)))
            } catch (ignored: JsonDataException) {
                App.tracker.trackException("invalid main.json", false)
            }
        }

        if (result == null && File(pathForID, "data.json").exists()) {
            result = PassReader.read(pathForID)
            File(pathForID, "data.json").delete()
        }

        if (result == null && File(pathForID, "pass.json").exists()) {
            result = AppleStylePassReader.read(pathForID, language, context)
        }

        if (result != null) {
            if (dirty) {
                save(result)
            }
            passMap.put(id, result)
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
        bus.post(PassStoreChangeEvent)
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
