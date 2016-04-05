package org.ligi.passandroid.unittest;

import java.util.HashMap;
import org.junit.Test;
import org.ligi.passandroid.model.PassClassifier;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.model.pass.PassImpl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThePassClassifier {

    public static final String ID_1 = "ID1";
    public static final String TOPIC_1 = "topic1";
    public static final String DEFAULT_TOPIC = "defaut_topic";

    private PassStore getMockedPassStore() {
        final PassStore mock = mock(PassStore.class);

        when(mock.getPassbookForId(anyString())).thenReturn(mock(Pass.class));

        return mock;
    }

    @Test
    public void testThatPassIsInactiveByDefault() {

        final PassClassifier tested = new PassClassifier(new HashMap<String, String>(), getMockedPassStore());

        assertThat(tested.getTopic(getPassWithId(ID_1),DEFAULT_TOPIC).equals(DEFAULT_TOPIC));
    }

    @Test
    public void testThatOnlyNonDefaultTopicInTopicListWhenOnePassWithNonDefaultTopic() {
        final PassClassifier tested = new PassClassifier(new HashMap<String, String>() {
            {
                put(ID_1, TOPIC_1);
            }
        }, getMockedPassStore());

        assertThat(tested.getTopics()).containsExactly(TOPIC_1);
    }

    @Test
    public void testThatAfterMovingFromOnlyOneTopicToDefaultTopicOnly() {
        final PassClassifier tested = new PassClassifier(new HashMap<String, String>() {
            {
                put(ID_1, TOPIC_1);
            }
        }, getMockedPassStore());

        tested.moveToTopic(getPassWithId(ID_1),DEFAULT_TOPIC);
        assertThat(tested.getTopics()).containsExactly(DEFAULT_TOPIC);
    }


    @Test
    public void testThatTopicIsGoneAfterMove() {
        final PassClassifier tested = new PassClassifier(new HashMap<String, String>() {
            {
                put(ID_1, TOPIC_1);
            }
        }, getMockedPassStore());

        tested.moveToTopic(getPassWithId(ID_1), DEFAULT_TOPIC);
        assertThat(tested.getTopics()).containsExactly(DEFAULT_TOPIC);
    }

    @Test
    public void testThatPassIsInTopicAsExpected() {
        final PassClassifier tested = new PassClassifier(new HashMap<String, String>() {
            {
                put(ID_1, TOPIC_1);
            }
        }, getMockedPassStore());

        assertThat(tested.getTopic(getPassWithId(ID_1),DEFAULT_TOPIC).equals(DEFAULT_TOPIC));
    }


    @Test
    public void testHasAtLeastOneTopic() {
        final PassClassifier tested = new PassClassifier(new HashMap<String, String>(), getMockedPassStore());

        assertThat(tested.getTopics()).containsExactly(DEFAULT_TOPIC);
    }

    public Pass getPassWithId(String id) {
        return new PassImpl(id);
    }


}
