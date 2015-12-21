package org.ligi.passandroid.model;

import org.ligi.passandroid.model.comparator.PassSortOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PassStoreProjection {

    private List<FiledPass> passList = new ArrayList<>();

    private final PassStore passStore;
    private final String topic;
    private final PassSortOrder passSortOrder;

    public PassStoreProjection(final PassStore passStore, final String topic, PassSortOrder order) {

        this.passStore = passStore;
        this.topic = topic;
        this.passSortOrder = order;

        refresh();
    }

    public List<FiledPass> getPassList() {
        return passList;
    }

    public void refresh() {
        ArrayList<FiledPass> newPassList = new ArrayList<>();

        for (FiledPass filedPass : passStore.getPassList()) {
            if (passStore.getClassifier().getTopic(filedPass).equals(topic)) {
                newPassList.add(filedPass);
            }
        }

        Collections.sort(newPassList, passSortOrder.toComparator());

        passList = newPassList;
    }
}
