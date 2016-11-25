package org.ligi.passandroid

import android.content.Context
import android.content.SharedPreferences
import android.support.test.InstrumentationRegistry.getInstrumentation
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.model.PastLocationsStore
import org.ligi.passandroid.ui.PassViewActivity
import org.ligi.trulesk.TruleskActivityRule
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ThePastLocationsStore {

    @get:Rule
    var rule = TruleskActivityRule(PassViewActivity::class.java) {
        MockitoAnnotations.initMocks(this)
    }

    @Mock
    lateinit var tracker: Tracker

    val prefs: SharedPreferences by lazy { getInstrumentation().context.getSharedPreferences("" + System.currentTimeMillis() / 100000, Context.MODE_PRIVATE) }

    @After
    fun tearDown() {
        prefs.edit().clear().apply()
    }

    @Test
    fun testPastLocationsStoreShouldNeverContainMoreThanMaxElements() {
        val tested = PastLocationsStore(prefs, tracker)

        for (i in 0..PastLocationsStore.MAX_ELEMENTS * 2 - 1) {
            tested.putLocation("" + i)
        }

        assertThat(tested.locations.size).isEqualTo(PastLocationsStore.MAX_ELEMENTS)

    }

    @Test
    fun testPastLocationsStoreShouldStoreOnlyOneOfAKind() {
        val tested = PastLocationsStore(prefs, tracker!!)

        for (i in 0..2) {
            tested.putLocation("foo")
        }

        assertThat(tested.locations).containsOnly("foo")

    }

}
