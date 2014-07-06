package org.ligi.passandroid.ui;

import android.content.Context;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.TicketDefinitions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

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

    public static void processInputStream(final InputStream inputStream, final Context context, SuccessCallback onSuccessCallback, FailCallback failCallback) {
        try {
            final File tempFile = File.createTempFile("ins", "pass");
            AXT.at(inputStream).toFile(tempFile);
            processFile(tempFile.getAbsolutePath(), context, onSuccessCallback, failCallback);
            tempFile.delete();
        } catch (IOException e) {
            failCallback.fail("problem with temp file" + e);
        }
    }

    public static void processFile(final String zipFileString, final Context context, SuccessCallback onSuccessCallback, FailCallback failCallback) {

        String path = context.getCacheDir() + "/temp/" + UUID.randomUUID() + "/";

        final File dir_file = new File(path);
        dir_file.mkdirs();

        if (!dir_file.exists()) {
            failCallback.fail("Problem creating the temp dir: " + path);
            return;
        }

        try {
            final ZipFile zipFile = new ZipFile(zipFileString);
            zipFile.extractAll(path);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        JSONObject manifest_json;
        try {
            manifest_json = new JSONObject(AXT.at(new File(path + "/manifest.json")).readToString());
        } catch (Exception e) {
            failCallback.fail("Problem with manifest.json: " + e);
            return;
        }

        try {
            final String rename_str = TicketDefinitions.getPassesDir(context) + "/" + manifest_json.getString("pass.json");
            final File rename_file = new File(rename_str);

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

}
