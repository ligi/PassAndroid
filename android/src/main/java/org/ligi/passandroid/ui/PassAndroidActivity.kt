package org.ligi.passandroid.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.ligi.kaxt.recreateWhenPossible
import org.ligi.passandroid.App
import org.ligi.passandroid.Tracker
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.Settings
import javax.inject.Inject

open class PassAndroidActivity : AppCompatActivity() {

    @Inject
    lateinit var passStore: PassStore

    @Inject
    lateinit var settings: Settings

    @Inject
    lateinit var bus: EventBus

    @Inject
    lateinit var tracker: Tracker

    private var lastSetNightMode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component.inject(this)
    }

    override fun onResume() {
        super.onResume()

        if (lastSetNightMode != null && lastSetNightMode != settings.getNightMode()) {
            recreateWhenPossible()
        }
        lastSetNightMode = settings.getNightMode()
    }

}