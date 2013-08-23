package org.ligi.ticketviewer.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Helper to read the content of a File
 *
 * @author Marcus -ligi- Büschleb
 *         <p/>
 *         License GPLv3
 */
public class FileHelper {

    /**
     * reads a file to a string - with default encoding
     *
     * @param file
     * @return the content of the file
     * @throws IOException
     */
    public static String file2String(File file) throws IOException {
        return file2String(file, Charset.defaultCharset());

    }

    /**
     * reads a file to a string
     *
     * @param file
     * @return the content of the file
     * @throws IOException
     */
    public static String file2String(File file, Charset charset) throws IOException {


        FileInputStream stream = new FileInputStream(file);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size());

            String fhelper = charset.decode(bb).toString();

            return fhelper;
        } finally {
            stream.close();
        }

    }

}
