package org.ligi.passandroid.model.comparator

import org.ligi.passandroid.model.comparator.DirectionAwarePassByTimeComparator.Companion.DIRECTION_ASC
import org.ligi.passandroid.model.pass.Pass
import java.util.*

class PassByTypeFirstAndTimeSecondComparator : Comparator<Pass> {

    private val passByTimeComparator = DirectionAwarePassByTimeComparator(DIRECTION_ASC)

    override fun compare(lhs: Pass, rhs: Pass): Int {
        val compareResult = lhs.type.compareTo(rhs.type)

        return if (compareResult != 0) {
            compareResult
        } else {
            passByTimeComparator.compare(lhs, rhs)
        }
    }
}
