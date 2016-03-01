package org.ligi.passandroid

import android.app.Activity
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.squareup.spoon.Spoon
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.PassField
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.ui.PassEditActivity
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class TheFieldListEditFragment : BaseUnitTest(){

    @get:Rule
    val rule = ActivityTestRule(PassEditActivity::class.java, true, false)

    @Inject
    lateinit var passStore: PassStore

    private val field: PassField = PassField(null, "labelfieldcontent", "valuefieldcontent", false)

    fun start(): Activity {
        super.setUp()
        val build = DaggerTestComponent.create()
        build.inject(this)
        App.setComponent(build)

        val currentPass = passStore.currentPass as PassImpl

        currentPass.fields = arrayListOf(field)

        return rule.launchActivity(null)
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
        onView(withId(R.id.label_field_edit)).perform(click(), replaceText("newlabel"))
        assertThat(field.label).isEqualTo("newlabel")
    }

    @Test
    fun testThatChangingValueWorks() {

        start()

        onView(withId(R.id.value_field_edit)).perform(scrollTo())
        onView(withId(R.id.value_field_edit)).perform(click(), replaceText("newvalue"))
        assertThat(field.value).isEqualTo("newvalue")
    }

    /* TODO add tests for delete and add */

}
