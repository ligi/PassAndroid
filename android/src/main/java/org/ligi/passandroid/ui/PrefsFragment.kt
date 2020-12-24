package org.ligi.passandroid.ui

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceFragmentCompat
import org.koin.android.ext.android.inject
import org.ligi.passandroid.R
import org.ligi.passandroid.model.Settings
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class PrefsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    val settings : Settings by inject()

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
            @AppCompatDelegate.NightMode val nightMode = settings.getNightMode()

            if (nightMode == MODE_NIGHT_AUTO) {
                ensureDayNightWithPermissionCheck()
            }

            AppCompatDelegate.setDefaultNightMode(nightMode)
            activity?.let { ActivityCompat.recreate(it) }
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
        onRequestPermissionsResult(requestCode, grantResults)
    }

}
