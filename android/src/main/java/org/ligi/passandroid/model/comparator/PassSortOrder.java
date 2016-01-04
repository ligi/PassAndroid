package org.ligi.passandroid.model.comparator;

import org.ligi.passandroid.model.Pass;

import java.util.Comparator;

public enum PassSortOrder {
    DATE(0),
    TYPE(1);

    private final int i;

    PassSortOrder(int i) {
        this.i = i;
    }

    public int getInt() {
        return i;
    }

    public Comparator<Pass> toComparator() {
        switch (this) {
            case TYPE:
                return new PassByTypeFirstAndTimeSecondComparator();
            default:
            case DATE:
                return new PassByTimeComparator();
        }
    }
}
