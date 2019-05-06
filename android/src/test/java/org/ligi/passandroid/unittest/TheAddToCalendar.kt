package org.ligi.passandroid.unittest

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.passandroid.functions.createIntent
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.model.pass.PassLocation
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.threeten.bp.ZonedDateTime

const val DESCRIPTIONPROBE = "descriptionprobe"
const val LOCATIONPROBE = "locationprobe"

class TheAddToCalendar {

    val pass: Pass = mock(Pass::class.java).apply {
        `when`(description).thenReturn(DESCRIPTIONPROBE)
    }

    private val validTimeSpan = mock(PassImpl.TimeSpan::class.java).apply {
        `when`(from).thenReturn(ZonedDateTime.now())
        `when`(to).thenReturn(ZonedDateTime.now().plusHours(5))

    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowIllegalArgumentWhenNoFromOrTo() {
        createIntent(mock(Pass::class.java), mock(PassImpl.TimeSpan::class.java))
    }

    @Test
    fun descriptionShouldShow() {
        val tested = createIntent(pass, validTimeSpan)
        assertThat(tested.getStringExtra("title")).isEqualTo(DESCRIPTIONPROBE)
    }

    @Test
    fun typeIsCorrect() {
        val tested = createIntent(pass, validTimeSpan)
        assertThat(tested.type).isEqualTo("vnd.android.cursor.item/event")
    }

    @Test
    fun locationIsCorrect() {
        `when`(pass.locations).thenReturn(listOf(PassLocation().apply { name = LOCATIONPROBE }))

        val tested = createIntent(pass, validTimeSpan)
        assertThat(tested.getStringExtra("eventLocation")).isEqualTo(LOCATIONPROBE)
    }


}
