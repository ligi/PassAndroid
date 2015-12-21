package org.ligi.passandroid.helper;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.ligi.passandroid.R;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassClassifier;

public class MoveHelper {
    public static void moveWithUndoSnackbar(final PassClassifier passClassifier, final Pass pass, String topic, final Activity activity) {
        final String oldTopic = passClassifier.getTopic(pass);

        Snackbar.make(activity.getWindow().getDecorView().findViewById(R.id.fam), "Pass moved to " + topic, Snackbar.LENGTH_LONG)
                .setAction("undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passClassifier.moveToTopic(pass, oldTopic);
                    }
                })
                .show();
        passClassifier.moveToTopic(pass, topic);
    }
}
