package org.ligi.passandroid.injections

import org.ligi.passandroid.model.Pass
import org.ligi.passandroid.model.PassClassifier
import org.ligi.passandroid.model.PassStore
import java.io.File
import java.util.*

class FixedPassListPassStore(private val passes: List<Pass>) : PassStore {
    override var currentPass: Pass? = null

    override val passMap: Map<String, Pass> by lazy {
        val hashMap = HashMap<String, Pass>()

        passes.forEach { hashMap.put(it.id, it) }
        return@lazy hashMap
    }

    override fun deleteCacheForId(id: String) {

    }

    override fun getPassbookForId(id: String): Pass? {
        return passMap[id]
    }

    override val classifier: PassClassifier by lazy {
        PassClassifier(HashMap<String, String>(), this)
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

    override fun syncPassStoreWithClassifier(default: String) {
        // no effect in this impl
    }

}
