package org.ligi.passandroid

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.helper.ItemTouchHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.ligi.gobandroid_hd.base.PassandroidTestRule
import org.ligi.passandroid.helper.ScreenshotTaker
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.ui.PassListActivity
import org.ligi.passandroid.ui.PassListFragment
import javax.inject.Inject

class ThePassListSwiping {

    @get:Rule
    val rule: PassandroidTestRule<PassListActivity> = PassandroidTestRule(PassListActivity::class.java, false)

    @Inject
    lateinit var passStore: PassStore

    private val activity by lazy {
        val build = DaggerTestComponent.create()
        build.inject(this)
        App.setComponent(build)
        rule.launchActivity(null)
    }

    @Test
    fun testWeCanMoveToTrash() {
        fakeSwipeLeft()

        onView(withText(R.string.topic_trash)).perform(click())

        assertThat(passStore.classifier.getTopics()).containsExactly(activity.getString(R.string.topic_trash))
    }


    @Test
    fun testWeCanMoveToArchive() {
        fakeSwipeLeft()

        onView(withText(R.string.topic_archive)).perform(click())

        assertThat(passStore.classifier.getTopics()).containsExactly(activity.getString(R.string.topic_archive))
    }

    @Test
    fun testWeCanMoveToCustom() {
        val CUSTOM_PROBE = "FOO_PROBE"

        fakeSwipeLeft()

        onView(withId(R.id.new_topic_edit)).perform(typeText(CUSTOM_PROBE))

        onView(withText(android.R.string.ok)).perform(click())

        assertThat(passStore.classifier.getTopics()).containsExactly(CUSTOM_PROBE)
    }


    @Test
    fun testDialogOpensWhenSwipeLeft() {
        fakeSwipeLeft()
        onView(withText(R.string.move_to_new_topic)).check(matches(isDisplayed()))
    }

    @Test
    fun testDialogOpensWhenSwipeRight() {

        fakeSwipeRight()

        ScreenshotTaker.takeScreenshot(activity, "move_to_new_topic_dialog")

        onView(withText(R.string.move_to_new_topic)).check(matches(isDisplayed()))
    }

    /*
      we have to fake swiping as the espresso methods swipeLeft and swipeRight made
      these tests flaky - more info here:
      http://stackoverflow.com/questions/35397439/swipe-tests-flaky-on-recyclerview
     */
    private fun fakeSwipe(dir: Int) {
        activity.runOnUiThread {
            val fragment = activity.supportFragmentManager.fragments.firstOrNull { it is PassListFragment } as PassListFragment
            fragment.onSwiped(0, dir)
        }
    }

    private fun fakeSwipeRight() {
        fakeSwipe(ItemTouchHelper.RIGHT)
    }

    private fun fakeSwipeLeft() {
        fakeSwipe(ItemTouchHelper.LEFT)
    }

}
