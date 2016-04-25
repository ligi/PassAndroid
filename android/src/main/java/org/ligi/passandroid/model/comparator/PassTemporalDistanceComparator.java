package org.ligi.passandroid.model.comparator;

import org.ligi.passandroid.model.pass.Pass;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

public class PassTemporalDistanceComparator extends PassByTimeComparator {

    @Override
    public int compare(Pass lhs, Pass rhs) {
        final int nullCheck = calculateCompareForNullValues(lhs, rhs);
        if (nullCheck == NO_NULL_VALUE) {

            final Duration durationLeft = Duration.between(LocalDateTime.now(), lhs.getCalendarTimespan().getFrom()).abs();
            final Duration durationRight = Duration.between(LocalDateTime.now(), rhs.getCalendarTimespan().getFrom()).abs();

            return durationLeft.compareTo(durationRight);
        } else {
            return nullCheck;
        }
    }
}
