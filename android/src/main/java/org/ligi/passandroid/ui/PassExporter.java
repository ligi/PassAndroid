package org.ligi.passandroid.ui;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.ligi.passandroid.App;

import java.io.File;

public class PassExporter {

    private final File inputPath;
    public final String fullZipFileName;
    public Exception exception;

    public PassExporter(final File inputPath, final String fullZipFileName) {
        this.inputPath = inputPath;
        this.fullZipFileName = fullZipFileName + ".espass";
    }


    public void export() {
        final File file = new File(fullZipFileName);
        try {
            file.delete();
            file.getParentFile().mkdirs();
            final ZipFile zipFile = new ZipFile(fullZipFileName);
            zipFile.createZipFileFromFolder(inputPath, new ZipParameters() {{
                setIncludeRootFolder(false);
                setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            }}, false, 0);

        } catch (Exception exception) {
            exception.printStackTrace();
            App.component().tracker().trackException("when exporting pass to zip", exception, false);
            this.exception = exception; // we need to take action on the main thread later
            file.delete(); // prevent zombies from taking over
        }
    }
}
