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

        if ((lhs.getCalendarTimespan() == null || lhs.getCalendarTimespan().getFrom() == null)
                && (rhs.getCalendarTimespan() == null || rhs.getCalendarTimespan().getFrom() == null)) {
            return 0;
        }

        if (lhs.getCalendarTimespan() == null || lhs.getCalendarTimespan().getFrom() == null) {
            return 1;
        }
        if (rhs.getCalendarTimespan() == null || rhs.getCalendarTimespan().getFrom() == null) {
            return -1;
        }
        return rhs.getCalendarTimespan().getFrom().compareTo(lhs.getCalendarTimespan().getFrom()) * direction;
    }
}
