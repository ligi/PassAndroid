package org.ligi.passandroid.ui

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.app.AppCompatDelegate.MODE_NIGHT_AUTO
import android.support.v7.preference.PreferenceFragmentCompat
import org.ligi.kaxt.recreateWhenPossible
import org.ligi.passandroid.App
import org.ligi.passandroid.R
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class PrefsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.preference_key_nightmode)) {

            @AppCompatDelegate.NightMode val nightMode = App.settings.getNightMode()

            if (nightMode == MODE_NIGHT_AUTO) {
                PrefsFragmentPermissionsDispatcher.ensureDayNightWithCheck(this)
            }

            AppCompatDelegate.setDefaultNightMode(nightMode)
            activity.recreateWhenPossible()
        }
    }


    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    fun ensureDayNight() {
        // Intentionally empty
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PrefsFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

}
