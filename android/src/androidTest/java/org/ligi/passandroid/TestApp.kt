package org.ligi.passandroid

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
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

class TestApp : App() {

    override fun createKodein() = Kodein.Module {
        bind<PassStore>() with singleton {
            FixedPassListPassStore(emptyList())
        }
        bind<Settings>() with singleton {
            mock(Settings::class.java).apply {
                `when`(getSortOrder()).thenReturn(PassSortOrder.DATE_ASC)
                `when`(getPassesDir()).thenReturn(File(""))
                `when`(doTraceDroidEmailSend()).thenReturn(false)
            }
        }
        bind<Tracker>(overrides = true) with singleton { mock(Tracker::class.java) }
        bind<EventBus>() with singleton { mock(EventBus::class.java) }
    }

    override fun installLeakCanary() = Unit

    companion object {


        fun passStore(): PassStore = kodein.instance()
        fun settings(): Settings = kodein.instance()

        fun populatePassStoreWithSinglePass() {

            val passList = ArrayList<Pass>()
            val pass = PassImpl(UUID.randomUUID().toString())
            pass.description = "description"
            pass.barCode = BarCode(PassBarCodeFormat.AZTEC, "messageprobe")
            passList.add(pass)

            fixedPassListPassStore().setList(passList)

            passStore().classifier.moveToTopic(pass, "test")
        }

        fun emptyPassStore() {
            fixedPassListPassStore().setList(emptyList())
        }

        fun fixedPassListPassStore() = passStore() as FixedPassListPassStore
    }
}
