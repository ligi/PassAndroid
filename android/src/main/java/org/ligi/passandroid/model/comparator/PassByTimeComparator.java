package org.ligi.passandroid.model.comparator;

import org.ligi.passandroid.model.Pass;

import java.util.Comparator;

public class PassByTimeComparator implements Comparator<Pass> {

    public final static int DIRECTION_DESC = 1;
    public final static int DIRECTION_ASC = -1;

    private final int direction;

    public PassByTimeComparator(int direction) {
        this.direction = direction;
    }


    @Override
    public int compare(Pass lhs, Pass rhs) {

        if (lhs.getRelevantDate() == null && rhs.getRelevantDate() == null) {
            return 0;
        }

        if (lhs.getRelevantDate() == null) {
            return 1;
        }
        if (rhs.getRelevantDate() == null) {
            return -1;
        }
        return rhs.getRelevantDate().compareTo(lhs.getRelevantDate()) * direction;
    }
}
