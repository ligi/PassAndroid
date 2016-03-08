package org.ligi.passandroid.ui;

import org.ligi.passandroid.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

class ExportProblemPassToLigiAndFinishTask extends PassExportTask {

    private final String reason;

    public ExportProblemPassToLigiAndFinishTask(Activity activity, String path, String zip_path, String zip_fname, final String reason) {
        super(activity, path, zip_path, zip_fname, false,PassExporter.FORMAT_OPENPASS);
        this.reason = reason;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "PassAndroid: Passbook with a problem");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ligi@ligi.de"});
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"  + passExporter.fullZipFileName));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "reason: " + reason);
        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.email_export_problem_title)));
        activity.finish();

    }
}
