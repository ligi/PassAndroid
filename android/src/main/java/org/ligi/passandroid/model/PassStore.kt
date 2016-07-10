package org.ligi.passandroid.model

import org.ligi.passandroid.model.pass.Pass
import java.io.File

interface PassStore {

    fun save(pass: Pass)

    fun getPassbookForId(id: String): Pass?

    fun deletePassWithId(id: String): Boolean

    fun getPathForID(id: String): File

    val passMap: Map<String, Pass>

    var currentPass: Pass?

    val classifier: PassClassifier

    fun notifyChange()

    fun syncPassStoreWithClassifier(defaultTopic: String)
}
