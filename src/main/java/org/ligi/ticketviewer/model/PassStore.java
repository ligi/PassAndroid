package org.ligi.ticketviewer.model;

import android.content.Context;

import org.ligi.ticketviewer.TicketDefinitions;
import org.ligi.ticketviewer.helper.DirectoryFileFilter;

import java.io.File;

public class PassStore {

    public final Context context;
    private String path;
    private String[] passIdents;

    public PassStore(Context context) {
        this.context = context;
        refreshPassesList();
    }

    public void refreshPassesList() {
        path = TicketDefinitions.getPassesDir(context);
        File passes_dir = new File(TicketDefinitions.getPassesDir(context));
        if (!passes_dir.exists()) {
            passes_dir.mkdirs();
        }
        passIdents = passes_dir.list(new DirectoryFileFilter());
    }

    public int passCount() {
        if (passIdents == null) {
            return 0;
        }

        return passIdents.length;
    }

    public boolean isEmpty() {
        return passCount() == 0;
    }

    public PassbookParser getPassbookAt(int pos) {
        String mPath = path + "/" + passIdents[pos];
        return new PassbookParser(mPath);
    }
}
