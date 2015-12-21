package org.ligi.passandroid.unittest;

import org.junit.Test;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.PassClassifier;
import org.ligi.passandroid.model.PassImpl;
import org.mockito.internal.util.collections.Sets;

import java.util.Collection;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class ThePassClassifier {

    public static final String ID_1 = "ID1";
    public static final String TOPIC_1 = "topic1";

    @Test
    public void testThatPassIsInactiveByDefault() {

        final PassClassifier tested = new PassClassifier(new HashMap<String, Collection<String>>());

        assertThat(tested.getTopic(getPassWithId(ID_1)).equals(PassClassifier.DEFAULT_TOPIC));
    }

    @Test
    public void testThatOnlyNonDefaultTopicInTopicListWhenOnePassWithNonDefaultTopic() {
        final PassClassifier tested = new PassClassifier(new HashMap<String, Collection<String>>() {
            {
                put(TOPIC_1, Sets.newSet(ID_1));
            }
        });

        assertThat(tested.getTopics()).containsExactly(TOPIC_1);
    }

    @Test
    public void testThatAfterMovingFromOnlyOneTopicToDefaultTopicOnly() {
        final PassClassifier tested = new PassClassifier(new HashMap<String, Collection<String>>() {
            {
                put(TOPIC_1, Sets.newSet(ID_1));
            }
        });

        tested.moveToTopic(getPassWithId(ID_1), PassClassifier.DEFAULT_TOPIC);
        assertThat(tested.getTopics()).containsExactly(PassClassifier.DEFAULT_TOPIC);
    }


    @Test
    public void testThatTopicIsGoneAfterMove() {
        final PassClassifier tested = new PassClassifier(new HashMap<String, Collection<String>>() {
            {
                put(TOPIC_1, Sets.newSet(ID_1));
            }
        });

        tested.moveToTopic(getPassWithId(ID_1), PassClassifier.DEFAULT_TOPIC);
        assertThat(tested.getTopics()).containsExactly(PassClassifier.DEFAULT_TOPIC);
    }

    @Test
    public void testThatPassIsInTopicAsExpected() {
        final PassClassifier tested = new PassClassifier(new HashMap<String, Collection<String>>() {
            {
                put(TOPIC_1, Sets.newSet(ID_1));
            }
        });

        assertThat(tested.getTopic(getPassWithId(ID_1)).equals(PassClassifier.DEFAULT_TOPIC));
    }


    @Test
    public void testHasAtLeastOneTopic() {
        final PassClassifier tested = new PassClassifier(new HashMap<String, Collection<String>>());

        assertThat(tested.getTopics()).containsExactly(PassClassifier.DEFAULT_TOPIC);
    }

    public FiledPass getPassWithId(String id) {
        PassImpl result = new PassImpl();
        result.setId(id);
        return result;
    }


}
