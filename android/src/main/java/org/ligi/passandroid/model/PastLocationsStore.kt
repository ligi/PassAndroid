package org.ligi.passandroid.model

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import org.ligi.passandroid.Tracker
import java.util.*

class PastLocationsStore constructor(private val sharedPreferences: SharedPreferences, private val tracker: Tracker) {

    fun putLocation(path: String) {
        if (Build.VERSION.SDK_INT < 11) {
            // feature not available for these versions
            return
        }
        val pastLocations = sharedPreferences.getStringSet(KEY_PAST_LOCATIONS, HashSet<String>())

        if (pastLocations!!.size >= MAX_ELEMENTS) {
            deleteOneElementFromSet(pastLocations)
        }

        if (!pastLocations.contains(path)) {
            pastLocations.add(path)
        }

        tracker.trackEvent("scan", "put location", "count", pastLocations.size.toLong())
        sharedPreferences.edit().putStringSet(KEY_PAST_LOCATIONS, pastLocations).apply()
    }

    private fun deleteOneElementFromSet(pastLocations: MutableSet<String>) {
        val deleteAtPosition = (Math.random() * MAX_ELEMENTS).toInt()
        for ((pos, location) in pastLocations.withIndex()) {
            if (pos == deleteAtPosition) {
                pastLocations.remove(location)
                return
            }
        }
    }

    // feature not available for these versions
    val locations: Set<String>
        @TargetApi(11)
        get() {
            if (Build.VERSION.SDK_INT < 11) {
                return HashSet()
            }
            return sharedPreferences.getStringSet(KEY_PAST_LOCATIONS, HashSet<String>())
        }

    companion object {

        val KEY_PAST_LOCATIONS = "past_locations"
        val MAX_ELEMENTS = 5
    }
}
