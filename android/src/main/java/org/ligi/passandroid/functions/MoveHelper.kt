package org.ligi.passandroid.functions

import android.app.Activity
import android.support.design.widget.Snackbar
import org.ligi.passandroid.R
import org.ligi.passandroid.model.PassClassifier
import org.ligi.passandroid.model.pass.Pass

fun moveWithUndoSnackbar(passClassifier: PassClassifier, pass: Pass, topic: String, activity: Activity) {
    val oldTopic = passClassifier.getTopic(pass, "")

    Snackbar.make(activity.window.decorView.findViewById(R.id.fam), "Pass moved to " + topic, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { passClassifier.moveToTopic(pass, oldTopic) }
            .show()
    passClassifier.moveToTopic(pass, topic)
}
