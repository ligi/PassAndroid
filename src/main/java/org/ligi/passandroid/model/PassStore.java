package org.ligi.passandroid.model;

import android.content.Context;

import org.ligi.axt.AXT;
import org.ligi.passandroid.TicketDefinitions;
import org.ligi.passandroid.helper.DirectoryFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PassStore {

    public final Context context;
    private String path;

    public enum SortOrder {
        DATE(0),
        TYPE(1);

        private final int i;

        SortOrder(int i) {
            this.i = i;
        }

        public int getInt() {
            return i;
        }

    }

    private SortOrder sortOrder;

    private List<ReducedPassInformation> reducedPassInformations;


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

        String[] passIdents = passes_dir.list(new DirectoryFileFilter());
        reducedPassInformations = new ArrayList<ReducedPassInformation>();

        for (String id : passIdents) {
            final File cachedFile = new File(path + "/" + id + "/base_cache.obj");

            ReducedPassInformation reducedPass = null;
            try {
                reducedPass = AXT.at(cachedFile).loadToObject();
            } catch (Exception e) {
            }

            if (reducedPass == null) {
                reducedPass = new ReducedPassInformation(new Passbook(path + "/" + id));
                AXT.at(cachedFile).writeObject(reducedPass);
            }

            reducedPassInformations.add(reducedPass);
        }

    }

    public int passCount() {
        return reducedPassInformations.size();
    }

    public boolean isEmpty() {
        return passCount() == 0;
    }

    public Passbook getPassbookAt(int pos) {
        String mPath = path + "/" + reducedPassInformations.get(pos).id;
        return new Passbook(mPath);
    }

    public ReducedPassInformation getReducedPassbookAt(int pos) {
        return reducedPassInformations.get(pos);
    }

    public void sort(SortOrder order) {
        switch (order) {
            case TYPE:
                Collections.sort(reducedPassInformations, new Comparator<ReducedPassInformation>() {
                    @Override
                    public int compare(ReducedPassInformation lhs, ReducedPassInformation rhs) {
                        if (lhs.type==null) {
                            return 1;
                        }
                        if (rhs.type==null) {
                            return -1;
                        }
                        return lhs.type.compareTo(rhs.type);
                    }
                });
                break;

            case DATE:
                Collections.sort(reducedPassInformations, new Comparator<ReducedPassInformation>() {
                    @Override
                    public int compare(ReducedPassInformation lhs, ReducedPassInformation rhs) {
                        if (lhs.relevantDate==null) {
                            return 1;
                        }
                        if (rhs.relevantDate==null) {
                            return -1;
                        }
                        return lhs.relevantDate.compareTo(rhs.relevantDate);
                    }
                });
                break;
        }

    }

}
