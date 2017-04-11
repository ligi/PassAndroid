package org.ligi.passandroid

import android.annotation.TargetApi
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.core.IsNot.not
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.model.pass.BarCode
import org.ligi.passandroid.model.pass.PassBarCodeFormat
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.model.pass.PassLocation
import org.ligi.passandroid.ui.PassViewActivity
import org.ligi.trulesk.TruleskActivityRule
import org.threeten.bp.ZonedDateTime
import java.util.*

@TargetApi(14)
class ThePassViewActivity {

    internal fun getActPass() = TestApp.passStore().currentPass as PassImpl

    @get:Rule
    var rule = TruleskActivityRule(PassViewActivity::class.java, false)

    @Test
    fun testThatDescriptionIsThere() {
        rule.launchActivity(null)

        onView(withText(getActPass().description)).check(matches(isDisplayed()))
    }

    @Test
    fun testDateIsGoneWhenPassbookHasNoDate() {
        getActPass().validTimespans = ArrayList<PassImpl.TimeSpan>()
        rule.launchActivity(null)

        onView(withId(R.id.date)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testEverythingWorksWhenWeHaveSomeLocation() {
        val timeSpen = ArrayList<PassLocation>()
        timeSpen.add(PassLocation())
        getActPass().locations = timeSpen
        rule.launchActivity(null)

        onView(withId(R.id.date)).check(matches(not(isDisplayed())))
    }


    @Test
    fun testDateIsThereWhenPassbookHasDate() {
        getActPass().calendarTimespan = PassImpl.TimeSpan(ZonedDateTime.now(), null, null)
        rule.launchActivity(null)

        onView(withId(R.id.date)).check(matches(isDisplayed()))
    }

    @Test
    fun testLinkToCalendarIsThereWhenPassbookHasDate() {
        getActPass().calendarTimespan = PassImpl.TimeSpan(ZonedDateTime.now(), null, null)
        rule.launchActivity(null)

        onView(withText(R.string.pass_to_calendar)).check(matches(isDisplayed()))
    }

    @Test
    fun testClickOnCalendarWithExpirationDateGivesWarning() {
        val validTimespans = ArrayList<PassImpl.TimeSpan>()
        validTimespans.add(PassImpl.TimeSpan(null, ZonedDateTime.now().minusHours(12), null))
        getActPass().validTimespans = validTimespans
        getActPass().calendarTimespan = null
        rule.launchActivity(null)

        onView(withText(R.string.pass_to_calendar)).perform(click())

        onView(withText(R.string.expiration_date_to_calendar_warning_message)).check(matches(isDisplayed()))
    }

    @Test
    fun testThatTheDialogCanBeDismissed() {
        testClickOnCalendarWithExpirationDateGivesWarning()

        onView(withText(android.R.string.cancel)).perform(click())

        onView(withText(R.string.expiration_date_to_calendar_warning_message)).check(doesNotExist())
    }

    @Test
    fun testLinkToCalendarIsNotThereWhenPassbookHasNoDate() {
        getActPass().validTimespans = ArrayList<PassImpl.TimeSpan>()
        rule.launchActivity(null)

        onView(withText(R.string.pass_to_calendar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testClickOnBarcodeOpensFullscreenImage() {
        getActPass().barCode = BarCode(PassBarCodeFormat.QR_CODE, "foo")
        rule.launchActivity(null)
        onView(withId(R.id.barcode_img)).perform(click())

        onView(withId(R.id.fullscreen_barcode)).check(matches(isDisplayed()))
    }

    @Test
    fun testZoomControlsAreThereWithBarcode() {
        getActPass().barCode = BarCode(PassBarCodeFormat.AZTEC, "foo")
        rule.launchActivity(null)

        onView(withId(R.id.zoomIn)).check(matches(isDisplayed()))
        onView(withId(R.id.zoomIn)).check(matches(isDisplayed()))
    }

    @Test
    fun testZoomControlsAreGoneWithoutBarcode() {
        getActPass().barCode = null
        rule.launchActivity(null)

        onView(withId(R.id.zoomIn)).check(matches(not(isDisplayed())))
        onView(withId(R.id.zoomIn)).check(matches(not(isDisplayed())))
    }

}
