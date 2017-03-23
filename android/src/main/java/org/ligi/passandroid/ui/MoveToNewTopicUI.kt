package org.ligi.passandroid.ui

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import org.ligi.passandroid.R
import org.ligi.passandroid.functions.moveWithUndoSnackbar
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass

internal class MoveToNewTopicUI(private val context: Activity, private val passStore: PassStore, private val pass: Pass) {

    fun show() {
        val dialog = AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.move_to_new_topic))
                .setView(R.layout.dialog_move_to_new_topic)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel) { _, _ -> passStore.notifyChange() }
                .setOnCancelListener { passStore.notifyChange() }
                .show()

        val move: (topic: String) -> Any = { topic ->
            moveWithUndoSnackbar(passStore.classifier, pass, topic, context)
            dialog.dismiss()
        }

        val newTopicEditText = dialog.findViewById(R.id.new_topic_edit) as EditText
        val suggestionButtonContainer= dialog.findViewById(R.id.topic_suggestions_button_container) as ViewGroup

        // we need to do this here so the dialog does not get dismissed
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (newTopicEditText.text.toString().isEmpty()) {
                newTopicEditText.error = context.getString(R.string.cannot_be_empty)
                newTopicEditText.requestFocus()
            } else {
                move(newTopicEditText.text.toString())
            }
        }

        val oldTopic = passStore.classifier.getTopic(pass, "")

        val suggestionTopicStringIds = intArrayOf(R.string.topic_trash, R.string.topic_archive, R.string.topic_new)

        suggestionTopicStringIds.map { context.getString(it) }.forEach {
            if (it != oldTopic) {
                val button = Button(context)
                button.text = it
                suggestionButtonContainer.addView(button)
                button.setOnClickListener { _ -> move(it) }
            }
        }
    }

}
