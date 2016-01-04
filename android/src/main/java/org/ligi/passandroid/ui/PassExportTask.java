package org.ligi.passandroid.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;

class PassExportTask extends AsyncTask<Void, Void, Void> {

    private final ProgressDialog progress_dialog;
    protected final Activity activity;

    private boolean share_after_export;
    protected PassExporter passExporter;

    public PassExportTask(final Activity activity,
                          final String inputPath,
                          final String zipPath,
                          final String zipFileName,
                          final boolean share_after_export,
                          @PassExporter.PassFormat final int passFormat) {
        this.activity = activity;
        passExporter = new PassExporter(passFormat, inputPath, zipPath + "/" + zipFileName);
        this.share_after_export = share_after_export;
        progress_dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        progress_dialog.setTitle(activity.getString(R.string.preparing_pass));
        progress_dialog.setMessage(activity.getString(R.string.please_wait));
        progress_dialog.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        passExporter.export();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (!activity.isFinishing() && progress_dialog.isShowing()) {
            progress_dialog.dismiss();
        }

        if (passExporter.exception != null) {
            App.component().tracker().trackException("passExporterException", passExporter.exception, false);
            Toast.makeText(activity, "could not export pass " + passExporter.exception, Toast.LENGTH_LONG).show();
            return;
        }

        if (share_after_export) {
            final Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.passbook_is_shared_subject));
            it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + passExporter.fullZipFileName));
            it.setType("application/vnd.apple.pkpass");
            activity.startActivity(Intent.createChooser(it, activity.getString(R.string.passbook_share_chooser_title)));
        }
    }

}
