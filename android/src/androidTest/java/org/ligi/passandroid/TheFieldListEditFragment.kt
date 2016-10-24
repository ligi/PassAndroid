package org.ligi.passandroid

import android.app.Activity
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import com.squareup.spoon.Spoon
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.base.PassandroidTestRule
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.PassField
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.ui.PassEditActivity
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class TheFieldListEditFragment {

    @get:Rule
    val rule: PassandroidTestRule<PassEditActivity> = PassandroidTestRule(PassEditActivity::class.java, false)

    @Inject
    lateinit var passStore: PassStore

    private val field: PassField = PassField(null, "labelfieldcontent", "valuefieldcontent", false)

    fun start(): Activity {
        val build = DaggerTestComponent.create()
        build.inject(this)
        App.setComponent(build)

        val currentPass = passStore.currentPass as PassImpl

        currentPass.fields = arrayListOf(field)

        val activity = rule.launchActivity(null)

        return activity
    }

    @Test
    fun testFieldDetailsArePreFilled() {
        Spoon.screenshot(start(), "one_field")

        onView(withId(R.id.label_field_edit)).perform(scrollTo())
        onView(withId(R.id.label_field_edit)).check(matches(isDisplayed()))
        onView(withId(R.id.label_field_edit)).check(matches(withText("labelfieldcontent")))

        onView(withId(R.id.value_field_edit)).check(matches(isDisplayed()))
        onView(withId(R.id.value_field_edit)).check(matches(withText("valuefieldcontent")))

    }


    @Test
    fun testThatChangingLabelWorks() {

        start()

        onView(withId(R.id.label_field_edit)).perform(scrollTo())
        onView(withId(R.id.label_field_edit)).perform(replaceText("newlabel"))
        assertThat(field.label).isEqualTo("newlabel")
    }

    @Test
    fun testThatChangingValueWorks() {

        start()

        onView(withId(R.id.value_field_edit)).perform(scrollTo())
        onView(withId(R.id.value_field_edit)).perform(replaceText("newvalue"))
        assertThat(field.value).isEqualTo("newvalue")
    }

    /* TODO add tests for delete and add */

}
