package org.ligi.passandroid.model.comparator;

import org.ligi.passandroid.model.pass.Pass;

public class DirectionAwarePassByTimeComparator extends PassByTimeComparator {

    public final static int DIRECTION_DESC = -1;
    public final static int DIRECTION_ASC = 1;

    private final int direction;

    public DirectionAwarePassByTimeComparator(int direction) {
        this.direction = direction;
    }

    @Override
    public int compare(Pass lhs, Pass rhs) {
        return super.compare(lhs, rhs) * direction;
    }
}
