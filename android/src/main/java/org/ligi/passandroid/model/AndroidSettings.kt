package org.ligi.passandroid.model

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate
import org.ligi.passandroid.R
import org.ligi.passandroid.R.string.preference_key_autolight
import org.ligi.passandroid.R.string.preference_key_condensed
import org.ligi.passandroid.model.comparator.PassSortOrder
import java.io.File

class AndroidSettings(val context: Context) : Settings {

    internal val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    override fun getSortOrder(): PassSortOrder {
        val key = context.getString(R.string.preference_key_sort)
        val stringValue = sharedPreferences.getString(key, "0")
        val id = Integer.valueOf(stringValue)!!

        return PassSortOrder.values().first { it.int == id }
    }

    override fun doTraceDroidEmailSend() = true

    override fun getPassesDir() = File(context.filesDir.absolutePath, "passes")

    override fun getStateDir() = File(context.filesDir, "state")

    override fun isCondensedModeEnabled() = sharedPreferences.getBoolean(context.getString(preference_key_condensed), false)

    override fun isAutomaticLightEnabled() = sharedPreferences.getBoolean(context.getString(preference_key_autolight), true)

    override fun getNightMode(): Int {
        val key = sharedPreferences.getString(context.getString(R.string.preference_key_nightmode), "auto")
        return when (key) {
            "day" -> AppCompatDelegate.MODE_NIGHT_NO
            "night" -> AppCompatDelegate.MODE_NIGHT_YES
            "auto" -> AppCompatDelegate.MODE_NIGHT_AUTO
            else -> AppCompatDelegate.MODE_NIGHT_AUTO
        }
    }

}
