package org.ligi.passandroid

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(TestModule::class))
interface TestComponent : AppComponent {

    fun inject(theFullscreenBarcodeActivity: TheFullscreenBarcodeActivity)

    fun inject(thePassEditActivity: ThePassEditActivity)

    fun inject(thePassViewActivity: ThePassViewActivity)

    fun inject(thePastLocationsStore: ThePastLocationsStore)

    fun inject(theBarCodeEditing: TheBarCodeEditing)

    fun inject(thePassListSwiping: ThePassListSwiping)

    fun inject(theFieldListEditFragment: TheFieldListEditFragment)
}
