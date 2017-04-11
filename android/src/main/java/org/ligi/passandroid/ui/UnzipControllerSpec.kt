package org.ligi.passandroid.ui

import android.content.Context
import org.ligi.passandroid.App
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.ui.UnzipPassController.FailCallback
import org.ligi.passandroid.ui.UnzipPassController.SuccessCallback
import java.io.File

open class UnzipControllerSpec(var targetPath: File,
                               val context: Context,
                               val passStore: PassStore,
                               val onSuccessCallback: SuccessCallback?,
                               val failCallback: FailCallback?) {
    var overwrite = false

    constructor(context: Context, passStore: PassStore, onSuccessCallback: SuccessCallback?, failCallback: FailCallback?)
            : this(App.settings.getPassesDir(), context, passStore, onSuccessCallback, failCallback)

}
