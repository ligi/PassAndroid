package org.ligi.passandroid

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import org.ligi.tracedroid.TraceDroid
import org.ligi.tracedroid.logging.Log

open class App : Application() {

    override fun onCreate() {
        super.onCreate()

        component = createComponent()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        installLeakCanary()
        AndroidThreeTen.init(this)
        initTraceDroid()

        AppCompatDelegate.setDefaultNightMode(component.settings().getNightMode())
    }

    open fun installLeakCanary() {
        LeakCanary.install(this)
    }

    open fun createComponent(): AppComponent {
        return DaggerAppComponent.builder().appModule(AppModule(this)).trackerModule(TrackerModule(this)).build()
    }

    private fun initTraceDroid() {
        TraceDroid.init(this)
        Log.setTAG("PassAndroid")
    }

    companion object {

        lateinit var component: AppComponent

    }
}
