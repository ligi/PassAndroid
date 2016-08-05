package org.ligi.passandroid.model

import org.ligi.passandroid.model.pass.Pass

open class PassClassifier(val topicByIdMap: MutableMap<String, String>, private val passStore: PassStore) {

    open fun processDataChange() {
        val topicsToRemove = topicByIdMap.filter { it.value.isEmpty() }.map { it.value }

        topicsToRemove.forEach {
            topicByIdMap.remove(it)
        }
        passStore.notifyChange()
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

    fun getTopic(pass: Pass, default: String): String {
        return getTopic(pass.id, default)
    }

    fun getTopic(id: String, default: String): String {
        val s = topicByIdMap[id]
        if (s != null) {
            return s
        }

        if (!default.isEmpty()) {
            topicByIdMap.put(id, default)
            processDataChange()
        }
        return default
    }

    fun removePass(id: String) {
        topicByIdMap.remove(id)
        processDataChange()
    }

    /*
    useful offsets are -1 and 1 to find the topic right and left from the pass
     */
    fun getTopicWithOffset(pass: Pass, offset: Int): String? {
        val indexOf = getTopics().indexOf(getTopic(pass, ""))
        return getTopics().elementAtOrNull(indexOf + offset)
    }

}
