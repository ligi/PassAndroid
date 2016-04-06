package org.ligi.passandroid.ui

import android.content.Context
import org.ligi.passandroid.App
import org.ligi.passandroid.model.PassStore
import java.io.File

open class UnzipControllerSpec(var targetPath: File,
                               val context: Context,
                               val passStore: PassStore,
                               val onSuccessCallback: UnzipPassController.SuccessCallback,
                               val failCallback: UnzipPassController.FailCallback) {
    var overwrite = false

    constructor(context: Context, passStore: PassStore, onSuccessCallback: UnzipPassController.SuccessCallback, failCallback: UnzipPassController.FailCallback) : this(App.component().settings().passesDir, context, passStore, onSuccessCallback, failCallback) {
    }

}
