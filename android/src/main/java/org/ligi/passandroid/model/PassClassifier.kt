package org.ligi.passandroid.model

open class PassClassifier(val topicByIdMap: MutableMap<String, String>, private val passStore: PassStore) {

    open fun processDataChange() {
        passStore.notifyChange()

        val topicsToRemove = topicByIdMap.filter { it.value.isEmpty() }.map { it.value }

        topicsToRemove.forEach {
            topicByIdMap.remove(it)
        }
    }

    fun moveToTopic(pass: Pass, newTopic: String) {
        topicByIdMap.put(pass.id, newTopic)

        processDataChange()
    }

    fun getTopics(): Set<String> {
        return topicByIdMap.values.toSet()
    }

    fun getPassListByTopic(topic: String): List<Pass> {
        return topicByIdMap.filter { it.value.equals(topic) }.map { passStore.getPassbookForId(it.key) }.filterNotNull()
    }

    fun getTopic(pass: Pass): String {
        return getTopic(pass.id)
    }

    fun getTopic(id: String): String {
        val s = topicByIdMap[id]
        if (s != null) {
            return s
        }

        topicByIdMap.put(id, DEFAULT_TOPIC)
        processDataChange()
        return DEFAULT_TOPIC
    }

    companion object {
        val DEFAULT_TOPIC = "active"
    }
}
