package org.ligi.passandroid

import android.app.Activity.RESULT_CANCELED
import android.app.Instrumentation
import android.provider.CalendarContract
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.intent.matcher.IntentMatchers.hasType
import android.support.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.functions.DEFAULT_EVENT_LENGTH_IN_HOURS
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.ui.PassListActivity
import org.ligi.trulesk.TruleskIntentRule
import org.threeten.bp.ZonedDateTime

class TheAddToCalendar {

    val time = ZonedDateTime.now()
    val time2 = ZonedDateTime.now().plusHours(3)

    @get:Rule
    var rule = TruleskIntentRule(PassListActivity::class.java, false)

    @Test
    fun testIfWeOnlyHaveCalendarStartDate() {
        TestApp.populatePassStoreWithSinglePass()

        TestApp.passStore().currentPass!!.calendarTimespan = PassImpl.TimeSpan(time)
        rule.launchActivity()

        intending(hasType("vnd.android.cursor.item/event")).respondWith(Instrumentation.ActivityResult(RESULT_CANCELED, null))

        onView(withId(R.id.timeButton)).perform(click())

        intended(allOf(
                hasType("vnd.android.cursor.item/event"),
                hasExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.toEpochSecond() * 1000),
                hasExtra(CalendarContract.EXTRA_EVENT_END_TIME, time.plusHours(DEFAULT_EVENT_LENGTH_IN_HOURS).toEpochSecond() * 1000),
                hasExtra("title", TestApp.passStore().currentPass!!.description)
        ))
    }

    @Test
    fun testIfWeOnlyHaveCalendarEndDate() {
        TestApp.populatePassStoreWithSinglePass()

        TestApp.passStore().currentPass!!.calendarTimespan = PassImpl.TimeSpan(to = time)
        rule.launchActivity()

        intending(hasType("vnd.android.cursor.item/event")).respondWith(Instrumentation.ActivityResult(RESULT_CANCELED, null))

        onView(withId(R.id.timeButton)).perform(click())

        intended(allOf(
                hasType("vnd.android.cursor.item/event"),
                hasExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.minusHours(DEFAULT_EVENT_LENGTH_IN_HOURS).toEpochSecond() * 1000),
                hasExtra(CalendarContract.EXTRA_EVENT_END_TIME, time.toEpochSecond() * 1000),
                hasExtra("title", TestApp.passStore().currentPass!!.description)
        ))
    }

    @Test
    fun testIfWeOnlyHaveCalendarStartAndEndDate() {
        TestApp.populatePassStoreWithSinglePass()

        TestApp.passStore().currentPass!!.calendarTimespan = PassImpl.TimeSpan(time, time2)
        rule.launchActivity()

        intending(hasType("vnd.android.cursor.item/event")).respondWith(Instrumentation.ActivityResult(RESULT_CANCELED, null))

        onView(withId(R.id.timeButton)).perform(click())

        intended(allOf(
                hasType("vnd.android.cursor.item/event"),
                hasExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.toEpochSecond() * 1000),
                hasExtra(CalendarContract.EXTRA_EVENT_END_TIME, time2.toEpochSecond() * 1000),
                hasExtra("title", TestApp.passStore().currentPass!!.description)
        ))
    }


    @Test
    fun testIfWeOnlyHaveExpirationDate() {
        TestApp.populatePassStoreWithSinglePass()

        (TestApp.passStore().currentPass as PassImpl).validTimespans = listOf(PassImpl.TimeSpan(time))
        rule.launchActivity()

        intending(hasType("vnd.android.cursor.item/event")).respondWith(Instrumentation.ActivityResult(RESULT_CANCELED, null))

        onView(withId(R.id.timeButton)).perform(click())

        onView(withText(R.string.expiration_date_to_calendar_warning_title)).check(matches(isDisplayed()))
        onView(withText(android.R.string.ok)).perform(click())

        intended(allOf(
                hasType("vnd.android.cursor.item/event"),
                hasExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.toEpochSecond() * 1000),
                hasExtra(CalendarContract.EXTRA_EVENT_END_TIME, time.plusHours(DEFAULT_EVENT_LENGTH_IN_HOURS).toEpochSecond() * 1000),
                hasExtra("title", TestApp.passStore().currentPass!!.description)
        ))
    }

    @Test
    fun testIfWeOnlyHaveExpirationEndDate() {
        TestApp.populatePassStoreWithSinglePass()

        (TestApp.passStore().currentPass as PassImpl).validTimespans = listOf(PassImpl.TimeSpan(to = time))
        rule.launchActivity()

        intending(hasType("vnd.android.cursor.item/event")).respondWith(Instrumentation.ActivityResult(RESULT_CANCELED, null))

        onView(withId(R.id.timeButton)).perform(click())

        onView(withText(R.string.expiration_date_to_calendar_warning_title)).check(matches(isDisplayed()))
        onView(withText(android.R.string.ok)).perform(click())

        intended(allOf(
                hasType("vnd.android.cursor.item/event"),
                hasExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.minusHours(DEFAULT_EVENT_LENGTH_IN_HOURS).toEpochSecond() * 1000),
                hasExtra(CalendarContract.EXTRA_EVENT_END_TIME, time.toEpochSecond() * 1000),
                hasExtra("title", TestApp.passStore().currentPass!!.description)
        ))
    }

    @Test
    fun testIfWeOnlyHaveExpirationStartAndEndDate() {
        TestApp.populatePassStoreWithSinglePass()

        (TestApp.passStore().currentPass as PassImpl).validTimespans = listOf(PassImpl.TimeSpan(time, time2))
        rule.launchActivity()

        intending(hasType("vnd.android.cursor.item/event")).respondWith(Instrumentation.ActivityResult(RESULT_CANCELED, null))

        onView(withId(R.id.timeButton)).perform(click())

        onView(withText(R.string.expiration_date_to_calendar_warning_title)).check(matches(isDisplayed()))
        onView(withText(android.R.string.ok)).perform(click())

        intended(allOf(
                hasType("vnd.android.cursor.item/event"),
                hasExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.toEpochSecond() * 1000),
                hasExtra(CalendarContract.EXTRA_EVENT_END_TIME, time2.toEpochSecond() * 1000),
                hasExtra("title", TestApp.passStore().currentPass!!.description)
        ))
    }

    @Test
    fun testThereIsNoButtonWithNoDate() {
        TestApp.populatePassStoreWithSinglePass()
        rule.launchActivity()
        onView(withId(R.id.timeButton)).check(matches(not(isDisplayed())))
    }


}
