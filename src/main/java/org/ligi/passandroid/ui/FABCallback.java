package org.ligi.passandroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.helper.PassUtil;
import org.ligi.passandroid.model.FiledPass;

class FABCallback implements MaterialDialog.ListCallback {

    private final Activity activityContext;

    FABCallback(Activity activityContext) {
        this.activityContext = activityContext;
    }

    @Override
    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
        switch (i) {
            case 0: // scan
                final Intent intent = new Intent(activityContext, SearchPassesIntentService.class);
                activityContext.startService(intent);
                break;

            case 1: // demo-pass
                AXT.at(activityContext).startCommonIntent().openUrl("http://ligi.de/passandroid_samples/index.html");
                break;

            case 2: // add
                final FiledPass pass = PassUtil.createEmptyPass();
                App.getPassStore().setCurrentPass(pass);
                pass.save(App.getPassStore());
                AXT.at(activityContext).startCommonIntent().activityFromClass(PassEditActivity.class);
                break;

        }
    }
}
