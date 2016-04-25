package org.ligi.passandroid.model.comparator;

import java.util.Comparator;
import org.ligi.passandroid.model.pass.Pass;

public class PassByTimeComparator implements Comparator<Pass> {

    final int NO_NULL_VALUE = 5;

    @Override
    public int compare(Pass lhs, Pass rhs) {
        final int nullCheck = calculateCompareForNullValues(lhs, rhs);
        if (nullCheck == NO_NULL_VALUE) {
            return rhs.getCalendarTimespan().getFrom().compareTo(lhs.getCalendarTimespan().getFrom());
        } else {
            return nullCheck;
        }
    }

    public int calculateCompareForNullValues(final Pass lhs, final Pass rhs) {
        if ((lhs.getCalendarTimespan() == null || lhs.getCalendarTimespan().getFrom() == null) &&
            (rhs.getCalendarTimespan() == null || rhs.getCalendarTimespan().getFrom() == null)) {
            return 0;
        }

        if (lhs.getCalendarTimespan() == null || lhs.getCalendarTimespan().getFrom() == null) {
            return 1;
        }
        if (rhs.getCalendarTimespan() == null || rhs.getCalendarTimespan().getFrom() == null) {
            return -1;
        }
        return NO_NULL_VALUE;
    }
}
