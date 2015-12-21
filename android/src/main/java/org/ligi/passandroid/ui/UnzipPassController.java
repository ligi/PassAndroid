package org.ligi.passandroid.ui;

import android.content.Context;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.helper.SafeJSONReader;
import org.ligi.passandroid.model.InputStreamWithSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

public class UnzipPassController {

    public interface SuccessCallback {
        void call(String uuid);
    }

    public interface FailCallback {
        void fail(String reason);
    }

    public static void processInputStream(InputStreamUnzipControllerSpec spec) {
        try {
            final File tempFile = File.createTempFile("ins", "pass");
            AXT.at(spec.inputStreamWithSource.getInputStream()).toFile(tempFile);
            processFile(new FileUnzipControllerSpec(tempFile.getAbsolutePath(), spec));
            tempFile.delete();
        } catch (Exception e) {
            App.component().tracker().trackException("problem processing InputStream", e, false);
            spec.failCallback.fail("problem with temp file" + e);
        }
    }

    public static void processFile(FileUnzipControllerSpec spec) {

        String uuid = UUID.randomUUID().toString();
        String path = spec.context.getCacheDir() + "/temp/" + uuid + "/";

        final File dir_file = new File(path);
        dir_file.mkdirs();

        if (!dir_file.exists()) {
            spec.failCallback.fail("Problem creating the temp dir: " + path);
            return;
        }

        AXT.at(new File(path + "source.obj")).writeObject(spec.source);

        try {
            final ZipFile zipFile = new ZipFile(spec.zipFileString);
            zipFile.extractAll(path);
        } catch (ZipException e) {
            e.printStackTrace();
        }


        JSONObject manifest_json;
        try {
            final String readToString = AXT.at(new File(path + "/manifest.json")).readToString();
            manifest_json = SafeJSONReader.readJSONSafely(readToString);
        } catch (FileNotFoundException e) {
            spec.failCallback.fail("Pass contains no Manifest - or PassAndroid could not read it");
            return;
        } catch (Exception e) {
            spec.failCallback.fail("Problem with manifest.json: " + e);
            return;
        }


        try {
            uuid = manifest_json.getString("pass.json");
            final String rename_str = spec.targetPath + "/" + uuid;
            new File(spec.targetPath).mkdirs();
            final File rename_file = new File(rename_str);

            if (spec.overwrite && rename_file.exists()) {
                AXT.at(rename_file).deleteRecursive();
            }

            if (!rename_file.exists()) {
                new File(path + "/").renameTo(rename_file);
            } else {
                spec.failCallback.fail("Pass with same ID exists");
                return;
            }

        } catch (JSONException e) {
            spec.failCallback.fail("Problem with pass.json: " + e);
            return;
        }

        spec.onSuccessCallback.call(uuid);

    }

    public static class UnzipControllerSpec {
        public final Context context;
        public final SuccessCallback onSuccessCallback;
        public final FailCallback failCallback;
        public String targetPath;
        public boolean overwrite = false;

        public UnzipControllerSpec(String targetPath, Context context, SuccessCallback onSuccessCallback, FailCallback failCallback) {
            this.context = context;
            this.onSuccessCallback = onSuccessCallback;
            this.failCallback = failCallback;
            this.targetPath = targetPath;
        }

        public UnzipControllerSpec(Context context, SuccessCallback onSuccessCallback, FailCallback failCallback) {
            this(App.component().settings().getPassesDir(), context, onSuccessCallback, failCallback);
        }


    }

    public static class FileUnzipControllerSpec extends UnzipControllerSpec {
        public final String zipFileString;
        public final String source;

        public FileUnzipControllerSpec(final String fileName,
                                       final InputStreamUnzipControllerSpec spec) {
            super(spec.targetPath, spec.context, spec.onSuccessCallback, spec.failCallback);
            zipFileString = fileName;
            source = spec.inputStreamWithSource.getSource();
            overwrite = spec.overwrite;

        }
    }

    public static class InputStreamUnzipControllerSpec extends UnzipControllerSpec {
        final InputStreamWithSource inputStreamWithSource;

        public InputStreamUnzipControllerSpec(final InputStreamWithSource inputStreamWithSource,
                                              final Context context,
                                              final SuccessCallback onSuccessCallback,
                                              final FailCallback failCallback) {
            super(context, onSuccessCallback, failCallback);
            this.inputStreamWithSource = inputStreamWithSource;
        }


    }

}
