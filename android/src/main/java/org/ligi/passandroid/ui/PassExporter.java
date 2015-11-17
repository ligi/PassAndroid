package org.ligi.passandroid.ui;

import android.support.annotation.IntDef;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.ligi.passandroid.App;

import java.io.File;

public class PassExporter {

    @IntDef({FORMAT_OPENPASS, FORMAT_PKPASS})
    public @interface PassFormat {
    }

    public static final int FORMAT_OPENPASS = 0;
    public static final int FORMAT_PKPASS = 1;

    private final int format;

    private final String inputPath;
    public final String fullZipFileName;
    public Exception exception;

    public PassExporter(final @PassFormat int format, final String inputPath, final String fullZipFileName) {
        this.format = format;
        this.inputPath = inputPath;
        this.fullZipFileName = fullZipFileName + getSuffix();
    }

    public String getMimeType() {
        switch (format) {
            case FORMAT_PKPASS:
                return "application/as";
            default:
                return "application/open-pass";
        }
    }


    public String getSuffix() {
        switch (format) {
            case FORMAT_PKPASS:
                return ".pkpass";
            default:
                return ".ops";
        }
    }

    public void export() {
        try {
            new File(fullZipFileName).delete();
            final ZipFile zipFile = new ZipFile(fullZipFileName);
            zipFile.createZipFileFromFolder(inputPath, new ZipParameters() {{
                setIncludeRootFolder(false);
                setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            }}, false, 0);

        } catch (Exception exception) {
            App.component().tracker().trackException("when exporting pass to zip", exception, false);
            this.exception = exception; // we need to take action on the main thread later
            new File(fullZipFileName).delete(); // prevent zombies from taking over
        }
    }
}
