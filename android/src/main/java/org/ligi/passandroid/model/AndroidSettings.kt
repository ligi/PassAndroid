package org.ligi.passandroid.model

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.preference.PreferenceManager
import org.ligi.passandroid.R
import org.ligi.passandroid.R.string.preference_key_autolight
import org.ligi.passandroid.R.string.preference_key_condensed
import org.ligi.passandroid.model.comparator.PassSortOrder
import java.io.File

class AndroidSettings(val context: Context) : Settings {

    internal val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun getSortOrder(): PassSortOrder {
        val key = context.getString(R.string.preference_key_sort)
        val stringValue = sharedPreferences.getString(key, "0")
        val id = Integer.valueOf(stringValue)!!
        val order = PassSortOrder.values().firstOrNull() { it.int.equals(id) }
        return order?:PassSortOrder.DATE_ASC
    }

    override fun doTraceDroidEmailSend(): Boolean {
        // will be overridden in test-module
        return true
    }

    override fun getPassesDir(): File {
        return File(context.filesDir.absolutePath, "passes")
    }

    override fun getStateDir(): File {
        return File(context.filesDir, "state")
    }


    override fun getShareDir(): File {
        return File(Environment.getExternalStorageDirectory(), "tmp/passbook_share_tmp")
    }

    override fun isCondensedModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(context.getString(preference_key_condensed), false)
    }

    override fun isAutomaticLightEnabled(): Boolean {
        return sharedPreferences.getBoolean(context.getString(preference_key_autolight), true)
    }

}
