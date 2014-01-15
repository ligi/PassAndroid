package org.ligi.passandroid.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class PassExportTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progress_dialog;
    protected Context ctx;
    private String path;
    protected String zipFileName, zipPath;
    private boolean share_after_export;

    public PassExportTask(Context ctx, String path, String zip_path, String zip_fname, boolean share_after_export) {
        super();
        this.ctx = ctx;
        this.path = path;
        this.zipFileName = zip_fname;
        this.zipPath = zip_path;

        this.share_after_export = share_after_export;
    }

    @Override
    protected void onPreExecute() {
        progress_dialog = new ProgressDialog(ctx);
        progress_dialog.setTitle("Preparing Pass");
        progress_dialog.setMessage("please wait - shouldn't be long");
        progress_dialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (progress_dialog.isShowing()) {
            progress_dialog.dismiss();
        }

        if (share_after_export) {
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_SUBJECT, "a Passbook is shared with you");
            it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + zipPath + zipFileName));
            it.setType("application/vnd.apple.pkpass");
            ctx.startActivity(Intent.createChooser(it, "How to send Pass?"));
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        byte[] buf = new byte[1024];

        try {
            String destinationDir = zipPath;

            boolean result = (new File(destinationDir)).mkdirs();

            String zipFullFilename = destinationDir + "/" + zipFileName;

            System.out.println(result);

            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFullFilename));
            // Compress the files

            File[] files = new File(path).listFiles();
            for (File filename : files) {
                FileInputStream in = new FileInputStream(filename);
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(filename.getName()));
                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                // Complete the entry
                out.closeEntry();
                in.close();
            } // Complete the ZIP file
            out.close();

        } catch (IOException e) {
        }


        return null;
    }
}
