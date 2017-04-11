package org.ligi.passandroid

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton

fun createTrackerKodeinModule(context: Context) = Kodein.Module {
    bind<Tracker>() with singleton { NotTracker() }
}
