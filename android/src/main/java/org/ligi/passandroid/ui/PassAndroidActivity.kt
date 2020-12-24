package org.ligi.passandroid.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.koin.android.ext.android.inject
import org.ligi.passandroid.Tracker
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.Settings

open class PassAndroidActivity : AppCompatActivity() {

    val passStore: PassStore by inject()
    val settings: Settings by inject()
    val tracker: Tracker by inject()

    private var lastSetNightMode: Int? = null

    override fun onResume() {
        super.onResume()

        if (lastSetNightMode != null && lastSetNightMode != settings.getNightMode()) {
            ActivityCompat.recreate(this)
        }
        lastSetNightMode = settings.getNightMode()
    }

}