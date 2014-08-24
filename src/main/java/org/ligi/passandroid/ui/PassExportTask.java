package org.ligi.passandroid.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.ligi.passandroid.R;
import org.ligi.passandroid.Tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class PassExportTask extends AsyncTask<Void, Void, Void> {

    private final ProgressDialog progress_dialog;
    protected final Activity activity;
    private final String inputPath;
    protected final String zipFileName, zipPath;

    private boolean share_after_export;
    private Exception exception;

    public PassExportTask(final Activity activity,
                          final String inputPath,
                          final String zipPath,
                          final String zipFileName,
                          final boolean share_after_export) {
        super();
        this.activity = activity;
        this.inputPath = inputPath;
        this.zipFileName = zipFileName;
        this.zipPath = zipPath;

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
        byte[] buf = new byte[1024];

        try {
            final File zipPathFile = new File(zipPath);
            zipPathFile.mkdirs();

            final String zipFullFilename = zipPath + "/" + zipFileName;

            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFullFilename));
            // Compress the files

            final File[] files = new File(inputPath).listFiles();

            if (files == null) {
                throw new IllegalStateException("cannot listFiles in InputPath - is null");
            }

            for (File filename : files) {
                final FileInputStream in = new FileInputStream(filename);
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(filename.getName()));
                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
        } catch (IOException | IllegalStateException exception) {
            Tracker.get().trackException("when exporting pass to zip", exception, false);
            this.exception = exception; // we need to take action on the main thread later
            new File(zipFileName).delete(); // prevent zombies from taking over
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (!activity.isFinishing() && progress_dialog.isShowing()) {
            progress_dialog.dismiss();
        }

        if (exception != null) {
            Toast.makeText(activity, "could not export pass " + exception, Toast.LENGTH_LONG).show();
            return;
        }

        if (share_after_export) {
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.passbook_is_shared_subject));
            it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + zipPath + zipFileName));
            it.setType("application/vnd.apple.pkpass");
            activity.startActivity(Intent.createChooser(it, activity.getString(R.string.passbook_share_chooser_title)));
        }
    }

}
