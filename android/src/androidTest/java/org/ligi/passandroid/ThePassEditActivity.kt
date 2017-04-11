package org.ligi.passandroid

import android.annotation.TargetApi
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.espresso.matcher.ViewMatchers.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.R.id.*
import org.ligi.passandroid.R.string.*
import org.ligi.passandroid.model.pass.PassType.COUPON
import org.ligi.passandroid.model.pass.PassType.EVENT
import org.ligi.passandroid.ui.PassEditActivity
import org.ligi.trulesk.TruleskIntentRule

@TargetApi(14)
class ThePassEditActivity {

    val passStore = TestApp.passStore()

    @get:Rule
    var rule = TruleskIntentRule(PassEditActivity::class.java)

    @Test
    fun testSetToEventWorks() {

        onView(withId(categoryView)).perform(click())

        onView(withText(select_category_dialog_title)).perform(click())
        onView(withText(category_event)).perform(click())
        assertThat(passStore.currentPass!!.type).isEqualTo(EVENT)

        rule.screenShot("edit_set_event")
    }

    @Test
    fun testSetToCouponWorks() {
        onView(withId(categoryView)).perform(click())

        onView(withText(select_category_dialog_title)).perform(click())
        onView(withText(category_coupon)).perform(click())
        assertThat(passStore.currentPass!!.type).isEqualTo(COUPON)

        rule.screenShot("edit_set_coupon")
    }

    @Test
    fun testSetDescriptionWorks() {

        onView(withId(passTitle)).perform(clearText(), typeText("test description"))
        assertThat(passStore.currentPass!!.description).isEqualTo("test description")

        rule.screenShot("edit_set_description")
    }


    @Test
    fun testColorWheelIsThere() {

        onView(withId(categoryView)).perform(click())
        onView(withText(change_color_dialog_title)).perform(click())

        onView(withId(colorPicker)).check(matches(isDisplayed()))

        rule.screenShot("edit_set_color")
    }


    @Test
    fun testAddAbortFooterImagePick() {
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))

        onView(withId(R.id.add_footer)).perform(scrollTo(), click())

        intended(hasAction(Intent.ACTION_CHOOSER))
    }

    @Test
    fun testAddAbortStripImagePick() {
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))

        onView(withId(R.id.add_strip)).perform(scrollTo(), click())

        intended(hasAction(Intent.ACTION_CHOOSER))
    }

    @Test
    fun testAddAbortLogoImagePick() {

        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))

        onView(withId(R.id.add_logo)).perform(scrollTo(), click())

        intended(hasAction(Intent.ACTION_CHOOSER))
    }

}
