package org.ligi.ticketviewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipPasscodeDialog {

    public final static String TAG = "TicketViewer";

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
                    Log.i(TAG, "Decompress" + "unzip" + _location + ze.getName());
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
                Log.e("Decompress", "unzip", e);
            }

        }

        private void _dirChecker(String dir) {
            File f = new File(_location + dir);

            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }
    }

    /**
     * @param activity - if the alert should close when connection is established
     */
    public static void show(final InputStream ins, final String path, Activity activity, Callable<Void> intent_after_finish) {

        ProgressDialog dialog = ProgressDialog.show(activity, "", "Opening the Passbook. Please wait...", true);

        class AlertDialogUpdater implements Runnable {

            private ProgressDialog myProgress;
            private Callable intent_after_finish;

            public AlertDialogUpdater(Activity activity, ProgressDialog progress, Callable intent_after_finish) {
                this.intent_after_finish = intent_after_finish;
                myProgress = progress;
            }

            public void run() {
                new Decompress(ins, path).unzip();

                myProgress.dismiss();

                try {
                    intent_after_finish.call();
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                //   activity.startActivity(intent_after_finish);
                //	activity.finish();

            }
        }
        new Thread(new AlertDialogUpdater(activity, dialog, intent_after_finish)).start();

    }
}
