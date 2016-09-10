package org.ligi.passandroid.model.comparator;

import java.util.Comparator;
import org.ligi.passandroid.model.pass.Pass;
import static org.ligi.passandroid.model.comparator.DirectionAwarePassByTimeComparator.DIRECTION_ASC;

public class PassByTypeFirstAndTimeSecondComparator implements Comparator<Pass> {

    private final DirectionAwarePassByTimeComparator passByTimeComparator = new DirectionAwarePassByTimeComparator(DIRECTION_ASC);

    @Override
    public int compare(Pass lhs, Pass rhs) {
        final int compareResult = lhs.getType().compareTo(rhs.getType());

        if (compareResult != 0) {
            return compareResult;
        } else {
            return passByTimeComparator.compare(lhs, rhs);
        }
    }
}
