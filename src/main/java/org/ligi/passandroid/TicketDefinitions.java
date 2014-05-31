package org.ligi.passandroid;

import android.content.Context;
import android.os.Environment;

public class TicketDefinitions {

    public static String getPassesDir(final Context ctx) {
        return ctx.getFilesDir().getAbsolutePath() + "/passes";
    }

    public static String getShareDir() {
        return Environment.getExternalStorageDirectory() + "/tmp/passbook_share_tmp/";
    }
}
