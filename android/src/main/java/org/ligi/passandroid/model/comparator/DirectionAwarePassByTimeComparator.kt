package org.ligi.passandroid.model.comparator

import org.ligi.passandroid.model.pass.Pass

class DirectionAwarePassByTimeComparator(private val direction: Int) : PassByTimeComparator() {

    override fun compare(lhs: Pass, rhs: Pass): Int {
        return super.compare(lhs, rhs) * direction
    }

    companion object {
        val DIRECTION_DESC = -1
        val DIRECTION_ASC = 1
    }
}
