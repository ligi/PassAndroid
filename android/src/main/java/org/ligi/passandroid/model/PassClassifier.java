package org.ligi.passandroid.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class PassClassifier {

    public interface OnClassificationChangeListener {
        void OnClassificationChange();
    }

    public Set<OnClassificationChangeListener> onClassificationChangeListeners = new CopyOnWriteArraySet<>();

    public final static String DEFAULT_TOPIC = "active";

    protected final Map<String, Collection<String>> pass_id_list_by_topic;
    private final Map<String, String> topic_by_id = new HashMap<>();

    public PassClassifier(Map<String, Collection<String>> pass_id_list_by_topic) {
        this.pass_id_list_by_topic = pass_id_list_by_topic;

        processDataChange();
    }

    public void processDataChange() {
        calculateReverseMapping();
        removeEmpty();
        makeSureDefaultTopicExists();
    }

    private void calculateReverseMapping() {
        topic_by_id.clear();
        for (Map.Entry<String, Collection<String>> stringListEntry : pass_id_list_by_topic.entrySet()) {
            final String topic = stringListEntry.getKey();
            for (String id : stringListEntry.getValue()) {
                topic_by_id.put(id, topic);
            }
        }

    }

    private void makeSureDefaultTopicExists() {

        if (pass_id_list_by_topic.isEmpty()) {
            pass_id_list_by_topic.put(DEFAULT_TOPIC, new TreeSet<String>());
        }
    }

    private void removeEmpty() {
        final Set<String> toRemove = new HashSet<>();

        for (Map.Entry<String, Collection<String>> stringListEntry : pass_id_list_by_topic.entrySet()) {
            if (stringListEntry.getValue().isEmpty()) {
                toRemove.add(stringListEntry.getKey());
            }
        }

        for (String s : toRemove) {
            pass_id_list_by_topic.remove(s);
        }
    }

    public void moveToTopic(final Pass pass, final String newTopic) {
        if (topic_by_id.containsKey(pass.getId())) {
            final String oldTopic = topic_by_id.get(pass.getId());
            final Collection<String> idsForOldTopic = pass_id_list_by_topic.get(oldTopic);
            idsForOldTopic.remove(pass.getId());
            if (idsForOldTopic.isEmpty()) {
                pass_id_list_by_topic.remove(oldTopic);
            }

        }

        upsertPassToTopic(pass, newTopic);

        processDataChange();

        notifyDataChange();
    }

    public void notifyDataChange() {
        for (OnClassificationChangeListener onClassificationChangeListener : onClassificationChangeListeners) {
            onClassificationChangeListener.OnClassificationChange();
        }
    }

    private void upsertPassToTopic(Pass pass, String newTopic) {
        if (!pass_id_list_by_topic.containsKey(newTopic)) {
            pass_id_list_by_topic.put(newTopic, new TreeSet<String>());
        }

        final Collection<String> strings = pass_id_list_by_topic.get(newTopic);
        if (!strings.contains(pass.getId())) {
            strings.add(pass.getId());
            processDataChange();
        }
    }

    public String[] getTopics() {
        final Set<String> strings = pass_id_list_by_topic.keySet();
        return pass_id_list_by_topic.keySet().toArray(new String[strings.size()]);
    }

    public String getTopic(Pass pass) {
        if(topic_by_id.containsKey(pass.getId())) {
            return topic_by_id.get(pass.getId());
        }

        upsertPassToTopic(pass,DEFAULT_TOPIC);

        return DEFAULT_TOPIC;
    }
}
