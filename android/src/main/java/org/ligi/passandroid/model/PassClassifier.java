package org.ligi.passandroid.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PassClassifier {

    public interface OnClassificationChangeListener {
        void OnClassificationChange();
    }

    public Set<OnClassificationChangeListener> onClassificationChangeListeners = new CopyOnWriteArraySet<>();

    public String DEFAULT_TOPIC;

    protected final Map<String, String> topic_by_id;

    private final PassStore passStore;

    public PassClassifier(Map<String, String> topic_by_id, PassStore passStore, String defaultTopic) {
        this.topic_by_id = topic_by_id;
        this.passStore = passStore;
        this.DEFAULT_TOPIC = defaultTopic;

        processDataChange();
    }

    public void processDataChange() {

        final Set<String> keysToRemove = new HashSet<>();

        for (String key : topic_by_id.keySet()) {
            if (passStore.getPassbookForId(key) == null) {
                keysToRemove.add(key);
            }
        }

        for (String key : keysToRemove) {
            topic_by_id.remove(key);
        }

    }


    public void moveToTopic(final Pass pass, final String newTopic) {
        topic_by_id.put(pass.getId(), newTopic);

        processDataChange();

        notifyDataChange();
    }

    public void notifyDataChange() {
        for (OnClassificationChangeListener onClassificationChangeListener : onClassificationChangeListeners) {
            onClassificationChangeListener.OnClassificationChange();
        }
    }


    public Collection<String> getTopics() {
        final Collection<String> res = new HashSet<>();

        res.addAll(topic_by_id.values());

        if (res.isEmpty()) {
            res.add(DEFAULT_TOPIC);
        }

        return res;
    }

    public String getTopic(Pass pass) {
        if (topic_by_id.containsKey(pass.getId())) {
            return topic_by_id.get(pass.getId());
        }

        topic_by_id.put(pass.getId(),DEFAULT_TOPIC);

        return DEFAULT_TOPIC;
    }
}
