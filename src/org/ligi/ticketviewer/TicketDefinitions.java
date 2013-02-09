package org.ligi.ticketviewer;

import android.content.Context;

public class TicketDefinitions {

    public static String getPassesDir(Context ctx) {
        return ctx.getFilesDir().getAbsolutePath() + "/passes";
    }

}
