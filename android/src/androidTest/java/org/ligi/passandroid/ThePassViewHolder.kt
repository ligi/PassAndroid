package org.ligi.passandroid

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.model.pass.PassLocation
import org.ligi.passandroid.ui.PassListActivity
import org.ligi.trulesk.TruleskActivityRule
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class ThePassViewHolder {

    private val currentPass by lazy {
        TestApp.populatePassStoreWithSinglePass()
        TestApp.passStore.currentPass as PassImpl
    }

    @get:Rule
    var rule = TruleskActivityRule(PassListActivity::class.java, false)

    @Test
    fun locationButtonShouldBeVisibleIfWeHaveALocation() {

        currentPass.locations = listOf(PassLocation())

        rule.launchActivity()

        onView(withId(R.id.locationButton)).check(ViewAssertions.matches(isDisplayed()))

        rule.screenShot("with_location")
    }


    @Test
    fun locationButtonShouldNotShowIfWeHaveNoLocation() {

        currentPass.locations = listOf()

        rule.launchActivity()

        onView(withId(R.id.locationButton)).check(ViewAssertions.matches(not(isDisplayed())))

        rule.screenShot("no_location")
    }


    @Test
    fun dateButtonShouldBeVisibleIfWeHaveADate() {
        currentPass.calendarTimespan = PassImpl.TimeSpan(ZonedDateTime.of(2016, 11, 23, 20, 42, 42, 5, ZoneId.systemDefault()))

        rule.launchActivity()

        onView(withId(R.id.timeButton)).check(ViewAssertions.matches(isDisplayed()))

        rule.screenShot("with_date")
    }


    @Test
    fun dateButtonShouldNotBeVisibleIfWeHaveNoDate() {
        currentPass.calendarTimespan = null

        rule.launchActivity()

        onView(withId(R.id.timeButton)).check(ViewAssertions.matches(not(isDisplayed())))

        rule.screenShot("no_date")
    }

}
