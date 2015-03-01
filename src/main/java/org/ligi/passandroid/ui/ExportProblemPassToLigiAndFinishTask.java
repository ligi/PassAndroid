package org.ligi.passandroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

class ExportProblemPassToLigiAndFinishTask extends PassExportTask {

    private final String reason;

    public ExportProblemPassToLigiAndFinishTask(Activity activity, String path, String zip_path, String zip_fname, final String reason) {
        super(activity, path, zip_path, zip_fname, false);
        this.reason = reason;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "PassAndroid: Passbook with a problem");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ligi@ligi.de"});
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + zipPath + zipFileName));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "reason: " + reason);
        activity.startActivity(Intent.createChooser(intent, "How to send Pass?"));
        activity.finish();

    }
}
