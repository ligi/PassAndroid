package org.ligi.passandroid

import android.os.Build
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.v7.app.AppCompatDelegate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.model.AndroidSettings
import org.ligi.passandroid.model.comparator.PassByTimeComparator
import org.ligi.passandroid.model.comparator.PassByTypeFirstAndTimeSecondComparator
import org.ligi.passandroid.model.comparator.PassTemporalDistanceComparator
import org.ligi.passandroid.ui.PreferenceActivity
import org.ligi.trulesk.TruleskActivityRule

class ThePreferenceActivity {

    @get:Rule
    val rule = TruleskActivityRule(PreferenceActivity::class.java)

    val androidSettings by lazy { AndroidSettings(rule.activity) }

    @Test
    fun autoLightToggles() {
        rule.screenShot("preferences")

        val automaticLightEnabled = androidSettings.isAutomaticLightEnabled()

        onView(withText(R.string.preference_autolight_title)).perform(click())

        assertThat(automaticLightEnabled).isEqualTo(!androidSettings.isAutomaticLightEnabled())

        onView(withText(R.string.preference_autolight_title)).perform(click())

        assertThat(automaticLightEnabled).isEqualTo(androidSettings.isAutomaticLightEnabled())

    }

    @Test
    fun condensedToggles() {

        val condensedModeEnabled = androidSettings.isCondensedModeEnabled()

        onView(withText(R.string.preference_condensed_title)).perform(click())

        assertThat(condensedModeEnabled).isEqualTo(!androidSettings.isCondensedModeEnabled())

        onView(withText(R.string.preference_condensed_title)).perform(click())

        assertThat(condensedModeEnabled).isEqualTo(androidSettings.isCondensedModeEnabled())

    }


    @Test
    fun weCanSetAllSortOrders() {

        val resources = rule.activity.resources
        val sortOrders = resources.getStringArray(R.array.sort_orders)
        sortOrders.forEach { sortOrder ->

            onView(withText(R.string.preference_sort_title)).perform(click())
            onView(withText(sortOrder)).perform(click())

            assertThat(androidSettings.getSortOrder().toComparator()).isInstanceOf(when (sortOrder) {
                resources.getString(R.string.sort_order_date_asc) -> PassByTimeComparator::class.java
                resources.getString(R.string.sort_order_date_desc) -> PassByTimeComparator::class.java
                resources.getString(R.string.sort_order_date_type) -> PassByTypeFirstAndTimeSecondComparator::class.java
                resources.getString(R.string.sort_order_date_temporaldistance) -> PassTemporalDistanceComparator::class.java
                else -> throw RuntimeException("unexpected sort order")
            })
        }
    }

    @Test
    fun weCanSetAllNightModes() {

        val resources = rule.activity.resources
        val sortOrders = resources.getStringArray(R.array.night_modes)

        sortOrders.filterNot { Build.VERSION.SDK_INT >= 21 && it == resources.getString(R.string.night_mode_auto) }.forEach { sortOrder ->

            onView(withText(R.string.preference_daynight_title)).perform(click())
            onView(withText(sortOrder)).perform(click())

            assertThat(androidSettings.getNightMode()).isEqualTo(when (sortOrder) {
                resources.getString(R.string.night_mode_day) -> AppCompatDelegate.MODE_NIGHT_NO
                resources.getString(R.string.night_mode_night) -> AppCompatDelegate.MODE_NIGHT_YES
                resources.getString(R.string.night_mode_auto) -> AppCompatDelegate.MODE_NIGHT_AUTO
                else -> throw RuntimeException("unexpected night-mode")
            })
        }
    }
}
