package org.ligi.passandroid

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.functions.checkThatHelpIsThere
import org.ligi.passandroid.ui.HelpActivity
import org.ligi.trulesk.TruleskActivityRule

class TheHelpActivity {

    @get:Rule
    val rule = TruleskActivityRule(HelpActivity::class.java)

    @Test
    fun testHelpIsThere() {
        checkThatHelpIsThere()
        rule.screenShot("help")
    }

    @Test
    fun test_that_help_finishes_on_home() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        assertThat(rule.activity.isFinishing).isTrue
    }

    @Test
    fun test_that_version_is_shown() {
        onView(withText("v" + BuildConfig.VERSION_NAME)).check(matches(isDisplayed()))
    }
}
