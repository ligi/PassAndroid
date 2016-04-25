package org.ligi.passandroid.model.comparator;

import java.util.Comparator;
import org.ligi.passandroid.model.pass.Pass;

public enum PassSortOrder {
    DATE_DESC(-1),
    DATE_ASC(0),
    TYPE(1),
    DATE_DIFF(2);

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
            case DATE_DESC:
                return new DirectionAwarePassByTimeComparator(DirectionAwarePassByTimeComparator.DIRECTION_DESC);

            case DATE_DIFF:
                return new PassTemporalDistanceComparator();

            default:
            case DATE_ASC:
                return new DirectionAwarePassByTimeComparator(DirectionAwarePassByTimeComparator.DIRECTION_ASC);
        }
    }
}
