package org.ligi.passandroid.model.comparator

import org.ligi.passandroid.model.pass.Pass
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime

class PassTemporalDistanceComparator : PassByTimeComparator() {

    override fun compare(lhs: Pass, rhs: Pass): Int {
        return calculateCompareForNullValues(lhs, rhs, { leftDate: ZonedDateTime, rightDate: ZonedDateTime ->
            val durationLeft = Duration.between(LocalDateTime.now(), leftDate).abs()
            val durationRight = Duration.between(LocalDateTime.now(), rightDate).abs()

            return@calculateCompareForNullValues durationLeft.compareTo(durationRight)
        })
    }
}
