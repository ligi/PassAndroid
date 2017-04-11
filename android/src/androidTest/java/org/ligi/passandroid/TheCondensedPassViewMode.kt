package org.ligi.passandroid

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.model.pass.PassField
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.ui.PassListActivity
import org.ligi.trulesk.TruleskActivityRule
import org.mockito.Mockito.`when`
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class TheCondensedPassViewMode {

    @get:Rule
    var rule = TruleskActivityRule(PassListActivity::class.java, false) {
        TestApp.populatePassStoreWithSinglePass()
        val currentPass = TestApp.passStore().currentPass as PassImpl
        currentPass.calendarTimespan = PassImpl.TimeSpan(ZonedDateTime.of(2016, 11, 23, 20, 42, 42, 5, ZoneId.systemDefault()))
        currentPass.fields = mutableListOf(PassField("textprobe", "bar", "yo", false))
    }

    @Test
    fun testDateShowsForCondensedOff() {
        `when`(TestApp.settings().isCondensedModeEnabled()).thenReturn(false)

        rule.launchActivity()

        onView(withId(R.id.date)).check(matches(withText(containsString("23"))))
        onView(withId(R.id.timeButton)).check(matches(withText(R.string.pass_to_calendar)))

        rule.screenShot("condensed_off")
    }


    @Test
    fun testFieldShowsForCondensedOn() {

        `when`(TestApp.settings().isCondensedModeEnabled()).thenReturn(true)

        rule.launchActivity()

        onView(withId(R.id.date)).check(matches(withText(containsString("bar"))))
        onView(withId(R.id.timeButton)).check(matches(withText(containsString("23"))))

        rule.screenShot("condensed_on")
    }

}
