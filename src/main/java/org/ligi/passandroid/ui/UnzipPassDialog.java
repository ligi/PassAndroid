package org.ligi.passandroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import org.ligi.axt.listeners.ActivityFinishingOnClickListener;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.InputStreamWithSource;

public class UnzipPassDialog {

    public static void DisplayError(final Activity activity, final String title, final String err) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(err)
                .setPositiveButton("OK", new ActivityFinishingOnClickListener(activity))
                .show();
    }

    public interface FinishCallback {
        public Void call(String path);
    }

    public static void show(final InputStreamWithSource ins, final Activity activity, final FinishCallback callAfterFinishOnUIThread) {
        if (activity.isFinishing()) {
            return; // no need to act any more ..
        }
        ProgressDialog dialog = ProgressDialog.show(activity, "", "Opening the Passbook. Please wait...", true);

        class AlertDialogUpdater implements Runnable {

            private final ProgressDialog myProgress;
            private final FinishCallback call_after_finish;

            public AlertDialogUpdater(Activity activity, ProgressDialog progress, FinishCallback call_after_finish) {
                this.call_after_finish = call_after_finish;
                myProgress = progress;
            }

            public void run() {
                UnzipPassController.processInputStream(ins, activity,
                        new UnzipPassController.SuccessCallback() {

                            @Override
                            public void call(final String pathToPassbook) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        call_after_finish.call(pathToPassbook);
                                    }
                                });
                            }
                        }
                        ,
                        new UnzipPassController.FailCallback() {
                            @Override
                            public void fail(final String reason) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (myProgress.isShowing()) {
                                            myProgress.dismiss();
                                            // TODO - check if it would be good to inform the user in another way
                                            DisplayError(activity, activity.getString(R.string.invalid_passbook_title), reason);
                                        }
                                    }
                                });
                            }
                        }
                );
            }
        }

        AlertDialogUpdater alertDialogUpdater = new AlertDialogUpdater(activity, dialog, callAfterFinishOnUIThread);
        new Thread(alertDialogUpdater).start();

    }

}
