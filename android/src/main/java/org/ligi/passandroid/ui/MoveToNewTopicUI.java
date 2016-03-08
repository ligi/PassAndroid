package org.ligi.passandroid.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.MoveHelper;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassStore;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

class MoveToNewTopicUI {

    private final Activity context;
    private final PassStore passStore;
    private final Pass pass;

    @Bind(R.id.new_topic_edit)
    EditText newTopicEditText;
    private AlertDialog dialog;

    @OnClick(R.id.suggestion_button_trash)
    void onTrashClick() {
        move(context.getString(R.string.topic_trash));
    }

    @OnClick(R.id.suggestion_button_archive)
    void onArchiveClick() {
        move(context.getString(R.string.topic_archive));
    }

    private void move(String topic) {
        MoveHelper.moveWithUndoSnackbar(passStore.getClassifier(context), pass, topic, context);

        dialog.dismiss();
    }

    private MoveToNewTopicUI(final Activity context, final PassStore passStore, final Pass pass) {
        this.context = context;
        this.passStore = passStore;
        this.pass = pass;

        dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_move_to_new_topic))
                .setView(R.layout.dialog_move_to_new_topic)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // empty but needed
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        passStore.getClassifier(context).notifyDataChange();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        passStore.getClassifier(context).notifyDataChange();
                    }
                })
                .show();

        // we need to do this here so the dialog does not get dismissed
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newTopicEditText.getText().toString().isEmpty()) {
                    newTopicEditText.setError(context.getString(R.string.exception_empty_string));
                    newTopicEditText.requestFocus();
                } else {
                    move(newTopicEditText.getText().toString());
                }
            }
        });
        ButterKnife.bind(this, dialog);

    }

    public static void show(final Activity context, final PassStore passStore, final Pass pass) {
        new MoveToNewTopicUI(context, passStore, pass);
    }
}
