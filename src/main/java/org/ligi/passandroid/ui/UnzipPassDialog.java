package org.ligi.passandroid.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import org.ligi.axt.listeners.ActivityFinishingOnClickListener;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.InputStreamWithSource;
import static org.ligi.passandroid.ui.UnzipPassController.FailCallback;
import static org.ligi.passandroid.ui.UnzipPassController.InputStreamUnzipControllerSpec;
import static org.ligi.passandroid.ui.UnzipPassController.SuccessCallback;
import static org.ligi.passandroid.ui.UnzipPassController.processInputStream;

public class UnzipPassDialog {

    public static void DisplayError(final Activity activity, final String title, final String err) {
        new AlertDialog.Builder(activity).setTitle(title)
                                         .setMessage(err)
                                         .setPositiveButton(android.R.string.ok, new ActivityFinishingOnClickListener(activity))
                                         .show();
    }

    public interface FinishCallback {
        Void call(String path);
    }

    public static void show(final InputStreamWithSource ins, final Activity activity, final FinishCallback callAfterFinishOnUIThread) {
        if (activity.isFinishing()) {
            return; // no need to act any more ..
        }
        final ProgressDialog dialog = ProgressDialog.show(activity,
                                                          activity.getString(R.string.unzip_pass_dialog_title),
                                                          activity.getString(R.string.unzip_pass_dialog_message),
                                                          true);
        dialog.setCancelable(false);

        class AlertDialogUpdater implements Runnable {

            private final FinishCallback call_after_finish;

            public AlertDialogUpdater(FinishCallback call_after_finish) {
                this.call_after_finish = call_after_finish;
            }

            public void run() {
                final InputStreamUnzipControllerSpec spec = new InputStreamUnzipControllerSpec(ins, activity, new SuccessCallback() {

                    @Override
                    public void call(final String uuid) {
                        if (activity.isFinishing()) {
                            return;
                        }

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                call_after_finish.call(uuid);
                            }
                        });
                    }
                }, new FailCallback() {
                    @Override
                    public void fail(final String reason) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (activity.isFinishing()) {
                                    return;
                                }
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                DisplayError(activity, activity.getString(R.string.invalid_passbook_title), reason);
                            }
                        });
                    }
                });
                processInputStream(spec);
            }
        }

        final AlertDialogUpdater alertDialogUpdater = new AlertDialogUpdater(callAfterFinishOnUIThread);
        new Thread(alertDialogUpdater).start();

    }

}
