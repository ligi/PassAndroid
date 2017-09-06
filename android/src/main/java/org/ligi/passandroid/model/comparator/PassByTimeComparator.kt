package org.ligi.passandroid.model.comparator

import org.ligi.passandroid.model.pass.Pass
import org.threeten.bp.ZonedDateTime
import java.util.*

open class PassByTimeComparator : Comparator<Pass> {

    override fun compare(lhs: Pass, rhs: Pass): Int {
        return calculateCompareForNullValues(lhs, rhs, { leftDate: ZonedDateTime, rightDate: ZonedDateTime ->
            return@calculateCompareForNullValues leftDate.compareTo(rightDate)
        })
    }

    protected fun calculateCompareForNullValues(lhs: Pass, rhs: Pass, foo: (leftDate: ZonedDateTime, rightDate: ZonedDateTime) -> Int): Int {
        val leftDate = extractPassDate(lhs)
        val rightDate = extractPassDate(rhs)

        if (leftDate == rightDate) {
            return 0
        }

        if (leftDate == null) {
            return 1
        }
        if (rightDate == null) {
            return -1
        }
        return foo(leftDate, rightDate)
    }

    private fun extractPassDate(pass: Pass): ZonedDateTime? {
        if (pass.calendarTimespan != null && pass.calendarTimespan!!.from != null) {
            return pass.calendarTimespan!!.from
        }

        if (pass.validTimespans != null && !pass.validTimespans!!.isEmpty()) {
            return pass.validTimespans!![0].from
        }

        return null
    }
}
