package org.ligi.passandroid

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.model.pass.PassField
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.ui.PassEditActivity
import org.ligi.trulesk.TruleskIntentRule
import java.util.*

class TheFieldListEditFragment {

    @get:Rule
    val rule = TruleskIntentRule(PassEditActivity::class.java) {
        TestApp.passStore().currentPass = PassImpl(UUID.randomUUID().toString()).apply {
            fields = arrayListOf(field)
        }
    }

    private val field: PassField = PassField(null, "labelfieldcontent", "valuefieldcontent", false)

    @Test
    fun testFieldDetailsArePreFilled() {

        rule.screenShot("one_field")

        onView(withId(R.id.label_field_edit)).perform(scrollTo())
        onView(withId(R.id.label_field_edit)).check(matches(isDisplayed()))
        onView(withId(R.id.label_field_edit)).check(matches(withText("labelfieldcontent")))

        onView(withId(R.id.value_field_edit)).check(matches(isDisplayed()))
        onView(withId(R.id.value_field_edit)).check(matches(withText("valuefieldcontent")))

    }


    @Test
    fun testThatChangingLabelWorks() {

        onView(withId(R.id.label_field_edit)).perform(scrollTo())
        onView(withId(R.id.label_field_edit)).perform(replaceText("newlabel"))
        assertThat(field.label).isEqualTo("newlabel")
    }

    @Test
    fun testThatChangingValueWorks() {

        onView(withId(R.id.value_field_edit)).perform(scrollTo())
        onView(withId(R.id.value_field_edit)).perform(replaceText("newvalue"))
        assertThat(field.value).isEqualTo("newvalue")
    }

    /* TODO add tests for delete and add */

}
