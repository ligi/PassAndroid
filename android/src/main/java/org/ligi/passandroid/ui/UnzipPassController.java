package org.ligi.passandroid.ui;

import android.content.Context;
import java.io.File;
import java.util.UUID;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.helper.SafeJSONReader;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.tracedroid.logging.Log;

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
            spec.getFailCallback().fail("problem with temp file" + e);
        }
    }

    public static void processFile(FileUnzipControllerSpec spec) {

        String uuid = UUID.randomUUID().toString();
        File path = new File(spec.getContext().getCacheDir(), "temp/" + uuid);

        path.mkdirs();

        if (!path.exists()) {
            spec.getFailCallback().fail("Problem creating the temp dir: " + path);
            return;
        }

        AXT.at(new File(path, "source.obj")).writeObject(spec.getSource());

        try {
            final ZipFile zipFile = new ZipFile(spec.getZipFileString());
            zipFile.extractAll(path.getAbsolutePath());
        } catch (ZipException e) {
            e.printStackTrace();
        }


        JSONObject manifest_json;
        final File manifestFile = new File(path, "manifest.json");
        final File espassFile = new File(path, "main.json");

        if (manifestFile.exists()) {
            try {
                final String readToString = AXT.at(manifestFile).readToString();
                manifest_json = SafeJSONReader.readJSONSafely(readToString);
                uuid = manifest_json.getString("pass.json");
            } catch (Exception e) {
                spec.getFailCallback().fail("Problem with manifest.json: " + e);
                return;
            }
        } else if (espassFile.exists()) {
            try {
                final String readToString = AXT.at(espassFile).readToString();
                manifest_json = SafeJSONReader.readJSONSafely(readToString);
                uuid = manifest_json.getString("id");
            } catch (Exception e) {
                spec.getFailCallback().fail("Problem with manifest.json: " + e);
                return;
            }
        } else {
            spec.getFailCallback().fail("Pass is not espass or pkpass format :-(");
            return;
        }


        final String rename_str = spec.getTargetPath() + "/" + uuid;
        new File(spec.getTargetPath()).mkdirs();
        final File rename_file = new File(rename_str);

        if (spec.getOverwrite() && rename_file.exists()) {
            AXT.at(rename_file).deleteRecursive();
        }

        if (!rename_file.exists()) {
            new File(path + "/").renameTo(rename_file);
        } else {
            Log.i("Pass with same ID exists");
        }


        spec.getOnSuccessCallback().call(uuid);

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
