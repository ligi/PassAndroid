package org.ligi.passandroid

import android.Manifest
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.linkedin.android.testbutler.TestButler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.PassBarCodeFormat
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.ui.PassEditActivity
import org.ligi.trulesk.TruleskActivityRule

@RunWith(AndroidJUnit4::class)
class TheBarCodeEditing {

    @get:Rule
    val rule = TruleskActivityRule(PassEditActivity::class.java, false)

    val passStore: PassStore = TestApp.passStore

    private lateinit var currentPass: PassImpl

    private fun start(setupPass: (pass: PassImpl) -> Unit = {}) {
        TestApp.populatePassStoreWithSinglePass()

        currentPass = passStore.currentPass as PassImpl

        setupPass(currentPass)

        TestButler.grantPermission(ApplicationProvider.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        TestButler.grantPermission(ApplicationProvider.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

        rule.launchActivity(null)
        closeSoftKeyboard()
    }

    @Test
    fun testNullBarcodeShowButtonAppears() {

        start {
            it.barCode = null
        }

        rule.screenShot("no_barcode")

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
        start()
        for (passBarCodeFormat in PassBarCodeFormat.values()) {
            onView(withId(R.id.barcode_img)).perform(scrollTo(), click())

            onView(withText(passBarCodeFormat.name)).perform(scrollTo(), click())

            onView(withId(R.id.randomButton)).perform(click())
            closeSoftKeyboard()

            onView(withText(android.R.string.ok)).perform(click())

            assertThat(currentPass.barCode!!.format).isEqualTo(passBarCodeFormat)
            rule.screenShot("edit_set_" + passBarCodeFormat.name)
        }
    }


    @Test
    fun testCanSetMessage() {
        start()

        onView(withId(R.id.barcode_img)).perform(click())

        onView(withId(R.id.messageInput)).perform(clearText())
        onView(withId(R.id.messageInput)).perform(replaceText("msg foo txt ;-)"))

        closeSoftKeyboard()

        onView(withText(android.R.string.ok)).perform(click())

        onView(withText(R.string.edit_barcode_dialog_title)).check(doesNotExist())

        assertThat(passStore.currentPass!!.barCode!!.message).isEqualTo("msg foo txt ;-)")
        rule.screenShot("edit_set_msg")
    }


    @Test
    fun testCanSetAltMessage() {
        start()

        onView(withId(R.id.barcode_img)).perform(click())

        onView(withId(R.id.alternativeMessageInput)).perform(clearText())
        onView(withId(R.id.alternativeMessageInput)).perform(replaceText("alt bar txt ;-)"))

        closeSoftKeyboard()

        onView(withText(android.R.string.ok)).perform(click())

        onView(withText(R.string.edit_barcode_dialog_title)).check(doesNotExist())

        assertThat(passStore.currentPass!!.barCode!!.alternativeText).isEqualTo("alt bar txt ;-)")
        rule.screenShot("edit_set_altmsg")
    }

    @Test
    fun testThatRandomChangesMessage() {
        start()

        onView(withId(R.id.barcode_img)).perform(click())

        val oldMessage = passStore.currentPass!!.barCode!!.message
        onView(withId(R.id.randomButton)).perform(click())

        closeSoftKeyboard()

        onView(withText(android.R.string.ok)).perform(click())

        assertThat(oldMessage).isNotEqualTo(passStore.currentPass!!.barCode!!.message)
    }

}
