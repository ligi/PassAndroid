package org.ligi.passandroid

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.linkedin.android.testbutler.TestButler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.model.pass.PassType.COUPON
import org.ligi.passandroid.model.pass.PassType.EVENT
import org.ligi.passandroid.ui.PassEditActivity
import org.ligi.trulesk.TruleskIntentRule

@TargetApi(14)
class ThePassEditActivity {

    val passStore = TestApp.passStore

    @get:Rule
    var rule = TruleskIntentRule(PassEditActivity::class.java) {
        TestApp.populatePassStoreWithSinglePass()
        TestButler.grantPermission(InstrumentationRegistry.getInstrumentation().targetContext, Manifest.permission.READ_EXTERNAL_STORAGE)
        TestButler.grantPermission(InstrumentationRegistry.getInstrumentation().targetContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    @Test
    fun testSetToEventWorks() {
        onView(withId(R.id.categoryView)).perform(click())

        onView(withText(R.string.select_category_dialog_title)).perform(click())
        onView(withText(R.string.category_event)).perform(click())
        assertThat(passStore.currentPass!!.type).isEqualTo(EVENT)

        rule.screenShot("edit_set_event")
    }

    @Test
    fun testSetToCouponWorks() {
        onView(withId(R.id.categoryView)).perform(click())

        onView(withText(R.string.select_category_dialog_title)).perform(click())
        onView(withText(R.string.category_coupon)).perform(click())
        assertThat(passStore.currentPass!!.type).isEqualTo(COUPON)

        rule.screenShot("edit_set_coupon")
    }

    @Test
    fun testSetDescriptionWorks() {

        onView(withId(R.id.passTitle)).perform(clearText(), replaceText("test description"))
        assertThat(passStore.currentPass!!.description).isEqualTo("test description")

        rule.screenShot("edit_set_description")
    }


    @Test
    fun testColorWheelIsThere() {

        onView(withId(R.id.categoryView)).perform(click())
        onView(withText(R.string.change_color_dialog_title)).perform(click())

        onView(withId(R.id.colorPicker)).check(matches(isDisplayed()))

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
