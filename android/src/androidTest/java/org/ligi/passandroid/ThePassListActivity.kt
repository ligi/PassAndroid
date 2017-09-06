package org.ligi.passandroid

import android.annotation.TargetApi
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.view.View
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.R.id.pass_recyclerview
import org.ligi.passandroid.functions.CollapsedCheck
import org.ligi.passandroid.functions.checkThatHelpIsThere
import org.ligi.passandroid.functions.expand
import org.ligi.passandroid.ui.PassListActivity
import org.ligi.trulesk.TruleskActivityRule

fun isCollapsed(): Matcher<in View>? = CollapsedCheck() as Matcher<in View>

@TargetApi(14)
class ThePassListActivity {

    @get:Rule
    var rule = TruleskActivityRule(PassListActivity::class.java) {
        TestApp.populatePassStoreWithSinglePass()
    }

    @Test
    fun testListIsThere() {

        onView(withId(pass_recyclerview)).check(matches(isDisplayed()))
        rule.screenShot("list")
    }

    @Test
    fun testHelpMenuBringsUsToHelp() {
        onView(withId(R.id.menu_help)).perform(click())

        checkThatHelpIsThere()
    }

    @Test
    fun testCloseFabOnBackPressed() {
        onView(withId(R.id.fam)).perform(expand())

        pressBack()

        onView(withId(R.id.fam))
                .check(matches(isDisplayed()))
                .check(matches(isCollapsed()))
    }

}
