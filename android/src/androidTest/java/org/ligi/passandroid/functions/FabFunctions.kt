package org.ligi.passandroid.functions

import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.view.View
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun expand(): ViewAction? = ExpandFabAction()

class ExpandFabAction : ViewAction {

    override fun getConstraints(): Matcher<View> = isAssignableFrom(FloatingActionsMenu::class.java)

    override fun getDescription() = "expands the floating action menu"

    override fun perform(uiController: UiController?, view: View?) {
        val fam = view as FloatingActionsMenu
        fam.expand()
    }

}

class CollapsedCheck : TypeSafeMatcher<FloatingActionsMenu>(FloatingActionsMenu::class.java) {

    override fun describeTo(description: Description?) {
        description?.appendText("is in collapsed state")
    }

    override fun matchesSafely(fam: FloatingActionsMenu?): Boolean {
        return !fam?.isExpanded!!
    }

}
