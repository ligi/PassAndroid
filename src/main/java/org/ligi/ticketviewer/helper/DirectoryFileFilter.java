package org.ligi.ticketviewer.helper;

import java.io.File;
import java.io.FilenameFilter;

public class DirectoryFileFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String filename) {
        return dir.isDirectory();
    }

}
