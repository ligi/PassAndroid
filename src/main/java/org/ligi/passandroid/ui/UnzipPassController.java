package org.ligi.passandroid.ui;

import android.content.Context;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.TicketDefinitions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static void processInputStream(final InputStream inputStream, final Context context, SuccessCallback onSuccessCallback, FailCallback failCallback) {
        try {
            final File tempFile = File.createTempFile("ins", "pass");
            AXT.at(inputStream).toFile(tempFile);
            processInputStream(tempFile.getAbsolutePath(), context, onSuccessCallback, failCallback);
            tempFile.delete();
        } catch (IOException e) {
            failCallback.fail("problem with temp file" + e);
        }
    }

    public static void processInputStream(final String zipFileString, final Context context, SuccessCallback onSuccessCallback, FailCallback failCallback) {

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
        private final InputStream zipFile;
        private final String location;

        public Decompress(InputStream zipFile, String location) {
            this.zipFile = zipFile;
            this.location = location;

            new File(location).mkdirs();
        }

        public void unzip() throws IOException {
            InputStream fin = zipFile;
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = zin.getNextEntry();

            byte[] buffer = new byte[1024];

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(location + File.separator + fileName);

                System.out.println("file unzip : " + newFile.getAbsoluteFile());

                // fix FileNotFoundException for compressed folders
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zin.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zin.getNextEntry();
            }

            zin.closeEntry();
            zin.close();

        }

    }

}
