package org.ligi.ticketviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.androidhelper.AndroidHelper;
import org.ligi.ticketviewer.helper.FileHelper;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipPasscodeDialog {

    public static void DisplayError(final Activity ctx, final String title, final String err) {

        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(ctx).setTitle(title).setMessage(err)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ctx.finish();
                            }

                        }).show();
            }
        });
    }

    public interface FinishCallback {
        public Void call(String path);
    }

    /**
     * @param activity - if the alert should close when connection is established
     */
    public static void show(final InputStream ins, final Activity activity, FinishCallback call_after_finish) {

        ProgressDialog dialog = ProgressDialog.show(activity, "", "Opening the Passbook. Please wait...", true);

        class AlertDialogUpdater implements Runnable {

            private ProgressDialog myProgress;
            private FinishCallback call_after_finish;

            public AlertDialogUpdater(Activity activity, ProgressDialog progress, FinishCallback call_after_finish) {
                this.call_after_finish = call_after_finish;
                myProgress = progress;
            }

            public void run() {
                String path = activity.getCacheDir() + "/temp/" + UUID.randomUUID() + "/";

                File dir_file = new File(path);
                dir_file.mkdirs();

                if (!dir_file.exists()) {
                    DisplayError(activity, "Problem", "Problem creating the temp dir: " + path);
                    return;
                }

                new Decompress(ins, path).unzip();

                Log.i("TicketView", "is temp");
                JSONObject manifest_json = null;
                try {
                    manifest_json = new JSONObject(FileHelper.file2String(new File(path + "/manifest.json")));
                } catch (Exception e) {
                    DisplayError(activity, "Invalid Passbook", "Problem with manifest.json: " + e);
                    //return false;
                    return;
                }

                try {
                    String rename_str = TicketDefinitions.getPassesDir(activity) + "/" + manifest_json.getString("pass.json");
                    File rename_file = new File(rename_str);
                    Log.i("Renaming to " + rename_str + " " + rename_file);

                    if (rename_file.exists()) {
                        AndroidHelper.at(rename_file).deleteRecursive();
                    }

                    new File(path + "/").renameTo(rename_file);
                    path = rename_str;
                } catch (JSONException e) {
                    DisplayError(activity, "Invalid Passbook", "Problem with pass.json: " + e);
                    return;
                }

                myProgress.dismiss();

                try {
                    call_after_finish.call(path);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }
        new Thread(new AlertDialogUpdater(activity, dialog, call_after_finish)).start();

    }

    public static class Decompress {
        private InputStream _zipFile;
        private String _location;

        public Decompress(InputStream zipFile, String location) {
            _zipFile = zipFile;
            _location = location;

            _dirChecker("");
        }

        public void unzip() {
            try {
                InputStream fin = _zipFile;
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                byte[] readData = new byte[1024];
                while ((ze = zin.getNextEntry()) != null) {
                    Log.i("Decompress" + "unzip" + _location + ze.getName());
                    if (ze.isDirectory()) {
                        _dirChecker(ze.getName());
                    } else {
                        FileOutputStream fout = new FileOutputStream(_location + ze.getName());

                        int i2 = zin.read(readData);

                        while (i2 != -1) {
                            fout.write(readData, 0, i2);
                            i2 = zin.read(readData);
                        }

                        zin.closeEntry();
                        fout.close();
                    }

                }
                zin.close();
            } catch (Exception e) {
                Log.e("unzip", e);
            }

        }

        private void _dirChecker(String dir) {
            File f = new File(_location + dir);

            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }
    }
}
