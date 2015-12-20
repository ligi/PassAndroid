package org.ligi.passandroid.model

import java.util.*

open class PassClassifier(protected val topic_by_id: MutableMap<String, String>, private val passStore: PassStore) {

    init {
        processDataChange()
    }

    open fun processDataChange() {

        val keysToRemove = topic_by_id.keys.filter { passStore.getPassbookForId(it) == null }

        for (key in keysToRemove) {
            topic_by_id.remove(key)
        }

    }

    fun moveToTopic(pass: Pass, newTopic: String) {
        topic_by_id.put(pass.id, newTopic)

        processDataChange()

        passStore.notifyChange()
    }

    val topics: Collection<String>
        get() {
            val res = HashSet<String>()

            res.addAll(topic_by_id.values)

            if (res.isEmpty()) {
                res.add(DEFAULT_TOPIC)
            }

            return res
        }

    fun getPassListByTopic(topic: String):List<Pass> {
        return topic_by_id.filter { it.value.equals(topic) }.map { passStore.getPassbookForId(it.key)!! }
    }

    fun getTopic(pass: Pass): String {
        val s = topic_by_id[pass.id]
        if (s != null) {
            return s
        }

        topic_by_id.put(pass.id, DEFAULT_TOPIC)

        return DEFAULT_TOPIC
    }

    companion object {
        val DEFAULT_TOPIC = "active"
    }
}
