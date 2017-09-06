package org.ligi.passandroid.unittest

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.ligi.passandroid.model.comparator.PassSortOrder
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.model.pass.PassType
import org.threeten.bp.ZonedDateTime
import java.util.*

class ThePassSorting {

    private val pass1 = PassImpl("ID1")
    private val pass2 = PassImpl("ID2")
    private val pass3 = PassImpl("ID3")
    private val pass4 = PassImpl("ID4")
    private val pass5 = PassImpl("ID5")

    lateinit private var passList: List<Pass>

    @Before
    fun init() {
        pass1.calendarTimespan = PassImpl.TimeSpan(ZonedDateTime.now().minusHours(5))
        pass3.calendarTimespan = PassImpl.TimeSpan(ZonedDateTime.now().plusHours(1))
        pass2.calendarTimespan = PassImpl.TimeSpan(ZonedDateTime.now().plusHours(2))
        pass5.validTimespans = listOf(PassImpl.TimeSpan(ZonedDateTime.now().plusHours(3)))

        pass1.type = PassType.GENERIC
        pass2.type = PassType.EVENT
        pass3.type = PassType.GENERIC
        pass4.type = PassType.GENERIC
        pass5.type = PassType.EVENT

        passList = mutableListOf(pass1, pass2, pass3, pass4, pass5)
    }

    @Test
    fun testDESC() {
        Collections.sort(passList, PassSortOrder.DATE_DESC.toComparator())

        assertThat(passList).containsExactly(pass4, pass5, pass2, pass3, pass1)
    }

    @Test
    fun testASC() {
        Collections.sort(passList, PassSortOrder.DATE_ASC.toComparator())

        assertThat(passList).containsExactly(pass1, pass3, pass2, pass5, pass4)
    }

    @Test
    fun testDIFF() {
        Collections.sort(passList, PassSortOrder.DATE_DIFF.toComparator())

        assertThat(passList).containsExactly(pass3, pass2, pass5, pass1, pass4)
    }

    @Test
    fun testTYPE() {
        Collections.sort(passList, PassSortOrder.TYPE.toComparator())

        assertThat(passList).containsExactly(pass1, pass3, pass4, pass2, pass5)
    }

}