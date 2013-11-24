package org.ligi.ticketviewer.ui;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.ticketviewer.TicketDefinitions;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipPassController {

    public interface SuccessCallback {
        public void call(String pathToPassbook);
    }


    public interface FailCallback {
        public void fail(String reason);
    }

    public static class SilentFail implements FailCallback {
        @Override
        public void fail(String reason) {
        }
    }

    public static class SilentWin implements SuccessCallback {
        @Override
        public void call(String path) {
        }
    }

    ;


    public static void processInputStream(final InputStream ins, final Context context, SuccessCallback onSuccessCallback, FailCallback failCallback) {

        String path = context.getCacheDir() + "/temp/" + UUID.randomUUID() + "/";

        File dir_file = new File(path);
        dir_file.mkdirs();

        if (!dir_file.exists()) {
            failCallback.fail("Problem creating the temp dir: " + path);
            return;
        }

        new Decompress(ins, path).unzip();

        JSONObject manifest_json;
        try {
            manifest_json = new JSONObject(AXT.at(new File(path + "/manifest.json")).loadToString());
        } catch (Exception e) {
            failCallback.fail("Problem with manifest.json: " + e);
            return;
        }

        try {
            String rename_str = TicketDefinitions.getPassesDir(context) + "/" + manifest_json.getString("pass.json");
            File rename_file = new File(rename_str);

            if (rename_file.exists()) {
                AXT.at(rename_file).deleteRecursive();
            }

            new File(path + "/").renameTo(rename_file);
            path = rename_str;
        } catch (JSONException e) {
            failCallback.fail("Problem with pass.json: " + e);
            return;
        }

        onSuccessCallback.call(path);

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
                ZipEntry ze;
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
