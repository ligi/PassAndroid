package org.ligi.passandroid

import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import org.ligi.passandroid.injections.FixedPassListPassStore
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.Settings
import org.ligi.passandroid.model.comparator.PassSortOrder
import org.ligi.passandroid.model.pass.BarCode
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassBarCodeFormat
import org.ligi.passandroid.model.pass.PassImpl
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.File
import java.util.*
import javax.inject.Singleton

@Module
class TestModule {

    private val passList: MutableList<Pass>

    constructor() {
        passList = ArrayList<Pass>()
        val pass = PassImpl(UUID.randomUUID().toString())
        pass.description = "description"
        pass.barCode = BarCode(PassBarCodeFormat.AZTEC, "messageprobe")
        passList.add(pass)

    }

    constructor(passList: MutableList<Pass>) {
        this.passList = passList
    }


    @Singleton
    @Provides
    fun providePassStore(): PassStore {
        val fixedPassListPassStore = FixedPassListPassStore(passList)
        if (!passList.isEmpty()) {
            fixedPassListPassStore.currentPass = passList[0]
        }

        for (pass in passList) {
            fixedPassListPassStore.classifier.moveToTopic(pass, "test")
        }
        return fixedPassListPassStore
    }

    @Singleton
    @Provides
    fun provideSettings(): Settings {
        val mock = mock(Settings::class.java)
        `when`(mock.getSortOrder()).thenReturn(PassSortOrder.DATE_ASC)
        `when`(mock.getPassesDir()).thenReturn(File(""))
        `when`(mock.doTraceDroidEmailSend()).thenReturn(false)
        return mock
    }


    @Singleton
    @Provides
    fun provideBus(): EventBus {
        return mock(EventBus::class.java)
    }


    @Singleton
    @Provides
    fun provideTracker(): Tracker {
        return mock(Tracker::class.java)
    }

}
