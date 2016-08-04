package org.ligi.passandroid

import android.support.test.espresso.Espresso.closeSoftKeyboard
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.SdkSuppress
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.squareup.spoon.Spoon
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.PassBarCodeFormat
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.ui.PassEditActivity
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class TheBarCodeEditing : BaseUnitTest() {

    @get:Rule
    val rule: ActivityTestRule<PassEditActivity> = ActivityTestRule(PassEditActivity::class.java, true, false)

    @Inject
    lateinit var passStore: PassStore

    lateinit var currentPass: PassImpl

    private val passEditActivity by lazy { rule.launchActivity(null) }

    fun start(setupPass: (pass: PassImpl) -> Unit) {

        val build = DaggerTestComponent.create()
        build.inject(this)
        App.setComponent(build)

        currentPass = passStore.currentPass as PassImpl

        setupPass(currentPass)

        setUp(passEditActivity)

        closeSoftKeyboard()
    }

    @Test
    fun testNullBarcodeShowButtonAppears() {

        start {
            it.barCode = null
        }

        Spoon.screenshot(passEditActivity!!, "no_barcode")

        onView(withId(R.id.add_barcode_button)).perform(scrollTo())
        onView(withId(R.id.add_barcode_button)).check(matches(isDisplayed()))
    }


    @Test
    fun testCreateBarcodeDefaultsToQR() {

        start {
            it.barCode = null
        }

        onView(withId(R.id.add_barcode_button)).perform(scrollTo(), click())

        closeSoftKeyboard()

        onView(withText(android.R.string.ok)).perform(click())

        assertThat(currentPass.barCode!!.format).isEqualTo(PassBarCodeFormat.QR_CODE)
    }

    @SdkSuppress(minSdkVersion = 14)
    @Test
    fun testCanSetToAllBarcodeTypes() {
        start {}
        for (passBarCodeFormat in PassBarCodeFormat.values()) {
            onView(withId(R.id.barcode_img)).perform(scrollTo(), click())

            onView(withText(passBarCodeFormat.name)).perform(scrollTo(), click())

            closeSoftKeyboard()

            onView(withText(android.R.string.ok)).perform(click())

            assertThat(currentPass.barCode!!.format).isEqualTo(passBarCodeFormat)
            Spoon.screenshot(passEditActivity!!, "edit_set_" + passBarCodeFormat.name)
        }
    }


    @Test
    fun testCanSetMessage() {
        start {}

        onView(withId(R.id.barcode_img)).perform(click())

        onView(withId(R.id.messageInput)).perform(clearText())
        onView(withId(R.id.messageInput)).perform(typeText("msg foo txt ;-)"))

        closeSoftKeyboard()

        onView(withText(android.R.string.ok)).perform(click())

        onView(withText(R.string.edit_barcode_dialog_title)).check(doesNotExist())

        assertThat(passStore.currentPass!!.barCode!!.message).isEqualTo("msg foo txt ;-)")
        Spoon.screenshot(passEditActivity!!, "edit_set_msg")
    }


    @Test
    fun testCanSetAltMessage() {
        start {}

        onView(withId(R.id.barcode_img)).perform(click())

        onView(withId(R.id.alternativeMessageInput)).perform(clearText())
        onView(withId(R.id.alternativeMessageInput)).perform(typeText("alt bar txt ;-)"))

        closeSoftKeyboard()

        onView(withText(android.R.string.ok)).perform(click())

        onView(withText(R.string.edit_barcode_dialog_title)).check(doesNotExist())

        assertThat(passStore.currentPass!!.barCode!!.alternativeText).isEqualTo("alt bar txt ;-)")
        Spoon.screenshot(passEditActivity!!, "edit_set_altmsg")
    }

    @Test
    fun testThatRandomChangesMessage() {
        start {}

        onView(withId(R.id.barcode_img)).perform(click())

        val oldMessage = passStore.currentPass!!.barCode!!.message
        onView(withId(R.id.randomButton)).perform(click())

        closeSoftKeyboard()

        onView(withText(android.R.string.ok)).perform(click())

        assertThat(oldMessage).isNotEqualTo(passStore.currentPass!!.barCode!!.message)
    }


}
