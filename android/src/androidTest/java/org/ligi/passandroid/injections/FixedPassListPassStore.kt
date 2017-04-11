package org.ligi.passandroid.injections

import org.ligi.passandroid.model.PassClassifier
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass
import java.io.File

class FixedPassListPassStore(private var passes: List<Pass>) : PassStore {

    override lateinit var classifier: PassClassifier

    init {
        classifier = PassClassifier(HashMap<String, String>(), this)
    }

    fun setList(newPasses: List<Pass>, newCurrentPass: Pass? = newPasses.firstOrNull()) {
        currentPass = newCurrentPass
        passes = newPasses
        passMap.clear()
        passMap.putAll(createHashMap())

        classifier = PassClassifier(HashMap<String, String>(), this)
    }

    override var currentPass: Pass? = null

    override val passMap: HashMap<String, Pass> by lazy {
        return@lazy createHashMap()
    }

    private fun createHashMap(): HashMap<String, Pass> {
        val hashMap = HashMap<String, Pass>()

        passes.forEach { hashMap.put(it.id, it) }
        return hashMap
    }

    override fun getPassbookForId(id: String): Pass? {
        return passMap[id]
    }


    override fun deletePassWithId(id: String): Boolean {
        return false
    }

    override fun getPathForID(id: String): File {
        return File("")
    }

    override fun save(pass: Pass) {
        // no effect in this impl
    }

    override fun notifyChange() {
        // no effect in this impl
    }

    override fun syncPassStoreWithClassifier(defaultTopic: String) {
        // no effect in this impl
    }

}
