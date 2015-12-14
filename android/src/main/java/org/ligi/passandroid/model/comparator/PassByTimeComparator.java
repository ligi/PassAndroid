package org.ligi.passandroid.model.comparator;

import org.ligi.passandroid.model.Pass;

import java.util.Comparator;

public class PassByTimeComparator implements Comparator<Pass> {

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
        return rhs.getRelevantDate().compareTo(lhs.getRelevantDate());
    }
}
