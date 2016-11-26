package org.ligi.passandroid

import dagger.Component
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.Settings
import org.ligi.passandroid.ui.*
import org.ligi.passandroid.ui.edit.FieldsEditFragment
import org.ligi.passandroid.ui.quirk_fix.USAirwaysLoadActivity
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, TrackerModule::class))
interface AppComponent {

    fun inject(passViewActivityBase: PassViewActivityBase)

    fun inject(passListActivity: PassListActivity)

    fun inject(passEditActivity: PassEditActivity)

    fun inject(passAdapter: PassAdapter)

    fun inject(passImportActivity: PassImportActivity)

    fun inject(passMenuOptions: PassMenuOptions)

    fun inject(searchPassesIntentService: SearchPassesIntentService)

    fun inject(usAirwaysLoadActivity: USAirwaysLoadActivity)

    fun inject(passAndroidActivity: PassAndroidActivity)

    fun inject(passListFragment: PassListFragment)

    fun inject(passNavigationView: PassNavigationView)

    fun inject(touchImageActivity: TouchImageActivity)

    fun inject(fieldsEditFragment: FieldsEditFragment)

    fun passStore(): PassStore

    fun tracker(): Tracker

    fun settings(): Settings

}
