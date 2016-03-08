package org.ligi.passandroid.helper;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.ligi.passandroid.R;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassClassifier;

import java.lang.String;

public class MoveHelper {
    public static void moveWithUndoSnackbar(final PassClassifier passClassifier, final Pass pass, String topic, final Activity activity) {
        final String oldTopic = passClassifier.getTopic(pass);

        Snackbar.make(activity.getWindow().getDecorView().findViewById(R.id.fam), String.format(activity.getString(R.string.notification_moved_to), topic), Snackbar.LENGTH_LONG)
                .setAction(activity.getString(R.string.action_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passClassifier.moveToTopic(pass, oldTopic);
                    }
                })
                .show();
        passClassifier.moveToTopic(pass, topic);
    }
}
