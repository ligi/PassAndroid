package org.ligi.passandroid

import android.annotation.TargetApi
import android.os.Build
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.R.id.pass_recyclerview
import org.ligi.passandroid.functions.checkThatHelpIsThere
import org.ligi.passandroid.functions.expand
import org.ligi.passandroid.functions.isCollapsed
import org.ligi.passandroid.ui.PassListActivity
import org.ligi.trulesk.TruleskActivityRule

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


    @Test
    fun testOpenVisibleOn19plus() {
        onView(withId(R.id.fam)).perform(expand())

        pressBack()

        if (Build.VERSION.SDK_INT >= 19) {
            onView(withId(R.id.fab_action_open_file)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.fab_action_open_file)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }

}
