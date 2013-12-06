package org.ligi.passandroid;

import android.content.Context;
import android.os.Environment;

public class TicketDefinitions {

    public static String getPassesDir(Context ctx) {
        return ctx.getFilesDir().getAbsolutePath() + "/passes";
    }

    public static String getShareDir(Context ctx) {
        return Environment.getExternalStorageDirectory() + "/tmp/passbook_share_tmp/";
    }
}
