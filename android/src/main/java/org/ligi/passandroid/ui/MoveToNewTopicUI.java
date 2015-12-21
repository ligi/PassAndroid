package org.ligi.passandroid.ui;

import android.app.Activity;
import android.content.DialogInterface;
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
        MoveHelper.moveWithUndoSnackbar(passStore.getClassifier(),pass,topic,context);

        dialog.dismiss();
    }

    MoveToNewTopicUI(Activity context, PassStore passStore, Pass pass) {
        this.context = context;
        this.passStore = passStore;
        this.pass = pass;
    }


    public void showTopicMove() {

        dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.move_to_new_topic))
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
                        passStore.getClassifier().notifyDataChange();
                    }
                })
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newTopicEditText.getText().toString().isEmpty()) {
                    newTopicEditText.setError("cannot be empty");
                    newTopicEditText.requestFocus();
                } else {
                    move(newTopicEditText.getText().toString());
                }
            }
        });
        ButterKnife.bind(this, dialog);
    }

}
