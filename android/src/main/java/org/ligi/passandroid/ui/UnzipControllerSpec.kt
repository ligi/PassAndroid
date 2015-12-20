package org.ligi.passandroid.ui

import android.content.Context
import org.ligi.passandroid.App

open class UnzipControllerSpec(var targetPath: String, val context: Context, val onSuccessCallback: UnzipPassController.SuccessCallback, val failCallback: UnzipPassController.FailCallback) {
    var overwrite = false

    constructor(context: Context, onSuccessCallback: UnzipPassController.SuccessCallback, failCallback: UnzipPassController.FailCallback) : this(App.component().settings().passesDir, context, onSuccessCallback, failCallback) {
    }

}
