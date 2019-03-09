package org.ligi.passandroid.unittest

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.passandroid.model.PassClassifier
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import org.mockito.Matchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*

const val ID_1 = "ID1"
const val TOPIC_1 = "topic1"
const val DEFAULT_TOPIC = "defaut_topic"

class ThePassClassifier {
    private val mockedPassStore by lazy {
        mock(PassStore::class.java).apply {
            `when`<Pass>(getPassbookForId(anyString())).thenReturn(mock(Pass::class.java))
        }
    }

    @Test
    fun testThatPassIsInactiveByDefault() {
        val tested = PassClassifier(HashMap(), mockedPassStore)

        assertThat(tested.getTopic(getPassWithId(ID_1), DEFAULT_TOPIC) == DEFAULT_TOPIC)
    }

    @Test
    fun testThatOnlyNonDefaultTopicInTopicListWhenOnePassWithNonDefaultTopic() {
        val tested = PassClassifier(mutableMapOf(ID_1 to TOPIC_1), mockedPassStore)

        assertThat(tested.getTopics()).containsExactly(TOPIC_1)
    }

    @Test
    fun testThatAfterMovingFromOnlyOneTopicToDefaultTopicOnly() {
        val tested = PassClassifier(mutableMapOf(ID_1 to TOPIC_1), mockedPassStore)

        tested.moveToTopic(getPassWithId(ID_1), DEFAULT_TOPIC)
        assertThat(tested.getTopics()).containsExactly(DEFAULT_TOPIC)
    }


    @Test
    fun testThatTopicIsGoneAfterMove() {
        val tested = PassClassifier(mutableMapOf(ID_1 to TOPIC_1), mockedPassStore)

        tested.moveToTopic(getPassWithId(ID_1), DEFAULT_TOPIC)
        assertThat(tested.getTopics()).containsExactly(DEFAULT_TOPIC)
    }

    @Test
    fun testThatPassIsInTopicAsExpected() {
        val tested = PassClassifier(mutableMapOf(ID_1 to TOPIC_1), mockedPassStore)

        assertThat(tested.getTopic(getPassWithId(ID_1), DEFAULT_TOPIC) == DEFAULT_TOPIC)
    }

    @Test
    fun testHasNoTopicsByDefault() {
        val tested = PassClassifier(HashMap(), mockedPassStore)

        assertThat(tested.getTopics()).isEmpty()
    }

    private fun getPassWithId(id: String) = PassImpl(id)

}
