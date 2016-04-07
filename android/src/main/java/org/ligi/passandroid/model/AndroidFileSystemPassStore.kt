package org.ligi.passandroid.model

import android.content.Context
import com.squareup.moshi.Moshi
import okio.Okio
import org.greenrobot.eventbus.EventBus
import org.ligi.axt.AXT
import org.ligi.passandroid.events.PassStoreChangeEvent
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.reader.AppleStylePassReader
import org.ligi.passandroid.reader.PassReader
import java.io.File
import java.util.*

class AndroidFileSystemPassStore(private val context: Context, settings: Settings, private val moshi: Moshi, private val bus: EventBus) : PassStore {
    private val path: File = settings.passesDir

    override val passMap = HashMap<String, Pass>()

    override var currentPass: Pass? = null

    override val classifier: PassClassifier by lazy {
        val classificationFile = File(settings.stateDir, "classifier_state.json")
        FileBackedPassClassifier(classificationFile, this, moshi)
    }

    override fun save(pass: Pass) {
        val jsonAdapter = moshi.adapter(PassImpl::class.java)

        val pathForID = getPathForID(pass.id)

        if (!pathForID.exists()) {
            pathForID.mkdirs()
        }

        val buffer = Okio.buffer(Okio.sink(File(pathForID, "main.json")))

        if (false) {
            val of = com.squareup.moshi.JsonWriter.of(buffer)
            of.setIndent("  ")
            jsonAdapter.toJson(of, pass as PassImpl)
            of.close()
        } else {
            jsonAdapter.toJson(buffer, pass as PassImpl)
            buffer.close()
        }

    }

    private fun readPass(id: String): Pass? {
        val pathForID = getPathForID(id)
        val language = context.resources.configuration.locale.language

        if (!pathForID.exists() || !pathForID.isDirectory) {
            return null;
        }

        val file = File(pathForID, "main.json")
        val result: Pass?
        var dirty = true
        if (file.exists()) {
            val jsonAdapter = moshi.adapter(PassImpl::class.java)
            dirty = false
            result = jsonAdapter.fromJson(Okio.buffer(Okio.source(file)))
        } else if (File(pathForID, "data.json").exists()) {
            result = PassReader.read(pathForID)
            File(pathForID, "data.json").delete()
        } else {
            result = AppleStylePassReader.read(pathForID, language)
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
        val result = AXT.at(getPathForID(id)).deleteRecursive()
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
