package org.ligi.passandroid.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.MoveHelper;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.Pass;

class MoveToNewTopicUI {

    private final Activity context;
    private final PassStore passStore;
    private final Pass pass;
    private AlertDialog dialog;

    @Bind(R.id.new_topic_edit)
    EditText newTopicEditText;

    @Bind(R.id.topic_suggestions_button_container)
    ViewGroup suggestionButtonContainer;

    private void move(String topic) {
        MoveHelper.moveWithUndoSnackbar(passStore.getClassifier(), pass, topic, context);

        dialog.dismiss();
    }

    private MoveToNewTopicUI(final Activity context, final PassStore passStore, final Pass pass) {
        this.context = context;
        this.passStore = passStore;
        this.pass = pass;

        dialog = new AlertDialog.Builder(context).setTitle(context.getString(R.string.move_to_new_topic))
                                                 .setView(R.layout.dialog_move_to_new_topic)
                                                 .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         // navigation_drawer_header but needed
                                                     }
                                                 })
                                                 .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         passStore.notifyChange();
                                                     }
                                                 })
                                                 .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                     @Override
                                                     public void onCancel(DialogInterface dialog) {
                                                         passStore.notifyChange();
                                                     }
                                                 })
                                                 .show();

        // we need to do this here so the dialog does not get dismissed
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

        final String oldTopic = passStore.getClassifier().getTopic(pass, "");

        int[] suggestionTopicStringIds = new int[]{R.string.topic_trash, R.string.topic_archive,R.string.topic_new};

        for (final int suggestionTopicStringId : suggestionTopicStringIds) {
            final String topic = context.getString(suggestionTopicStringId);
            if (!topic.equals(oldTopic)) {
                final Button button = new Button(context);
                button.setText(topic);
                suggestionButtonContainer.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        move(topic);
                    }
                });
            }
        }
    }

    public static void show(final Activity context, final PassStore passStore, final Pass pass) {
        new MoveToNewTopicUI(context, passStore, pass);
    }
}
