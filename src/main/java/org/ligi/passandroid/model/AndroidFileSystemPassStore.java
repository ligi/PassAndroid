package org.ligi.passandroid.model;

import android.content.Context;

import com.google.common.base.Optional;

import org.ligi.axt.AXT;
import org.ligi.passandroid.TicketDefinitions;
import org.ligi.passandroid.helper.DirectoryFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidFileSystemPassStore implements PassStore {

    private final Context context;
    private String path;

    private List<ReducedPassInformation> reducedPassInformationList;
    private Passbook actPass;

    public AndroidFileSystemPassStore(Context context) {
        this.context = context;
        refreshPassesList();
    }

    public void deleteCache() {

        String[] passIdents = getPassesDirSafely().list(new DirectoryFileFilter());

        for (String id : passIdents) {
            new File(path + "/" + id + "/base_cache.obj").delete();
        }
    }

    public void refreshPassesList() {
        path = TicketDefinitions.getPassesDir(context);

        File passes_dir = getPassesDirSafely();

        String[] passIdents = passes_dir.list(new DirectoryFileFilter());
        reducedPassInformationList = new ArrayList<>();

        for (String id : passIdents) {
            final File cachedFile = new File(path + "/" + id + "/base_cache.obj");

            ReducedPassInformation reducedPass = null;
            try {
                reducedPass = AXT.at(cachedFile).loadToObject();
            } catch (Exception e) {
            }

            if (reducedPass == null) {
                reducedPass = new ReducedPassInformation(new FilePathPassbook(path + "/" + id));
                AXT.at(cachedFile).writeObject(reducedPass);
            }

            reducedPassInformationList.add(reducedPass);
        }

    }

    private File getPassesDirSafely() {
        File passes_dir = new File(TicketDefinitions.getPassesDir(context));

        if (!passes_dir.exists()) {
            passes_dir.mkdirs();
        }
        return passes_dir;
    }

    public int passCount() {
        return reducedPassInformationList.size();
    }

    public boolean isEmpty() {
        return passCount() == 0;
    }

    public Passbook getPassbookAt(int pos) {
        return getPassbookForId(reducedPassInformationList.get(pos).id);
    }

    public Passbook getPassbookForId(String id) {
        String mPath = path + "/" + id;
        return new FilePathPassbook(mPath);
    }

    public ReducedPassInformation getReducedPassbookAt(int pos) {
        return reducedPassInformationList.get(pos);
    }

    public void sort(SortOrder order) {
        switch (order) {
            case TYPE:
                Collections.sort(reducedPassInformationList, new Comparator<ReducedPassInformation>() {
                    @Override
                    public int compare(ReducedPassInformation lhs, ReducedPassInformation rhs) {
                        if (lhs.type == rhs.type) { // that looks bad but makes sense for both being null
                            return 0;
                        }

                        if (lhs.type == null) {
                            return 1;
                        }
                        if (rhs.type == null) {
                            return -1;
                        }
                        return lhs.type.compareTo(rhs.type);
                    }
                });
                break;

            case DATE:
                Collections.sort(reducedPassInformationList, new Comparator<ReducedPassInformation>() {
                    @Override
                    public int compare(ReducedPassInformation lhs, ReducedPassInformation rhs) {
                        if (lhs.relevantDate == rhs.relevantDate) { // that looks bad but makes sense for both being null
                            return 0;
                        }

                        if (!lhs.relevantDate.isPresent()) {
                            return 1;
                        }
                        if (!rhs.relevantDate.isPresent()) {
                            return -1;
                        }
                        return rhs.relevantDate.get().compareTo(lhs.relevantDate.get());
                    }
                });
                break;
        }

    }

    public List<CountedType> getCountedTypes() {
        // TODO - some sort of caching
        Map<String, Integer> tempMap = new HashMap<String, Integer>();

        for (ReducedPassInformation info : reducedPassInformationList) {
            if (tempMap.containsKey(info.getTypeNotNull())) {
                Integer i = tempMap.get(info.getTypeNotNull());
                tempMap.put(info.getTypeNotNull(), i + 1);
            } else {
                tempMap.put(info.getTypeNotNull(), 1);
            }
        }

        List<CountedType> result = new ArrayList<CountedType>();

        for (String type : tempMap.keySet()) {
            result.add(new CountedType(type, tempMap.get(type)));
        }

        Collections.sort(result);

        return result;
    }

    @Override
    public Optional<Passbook> getCurrentPass() {
        if (actPass == null) {
            return Optional.absent();
        }

        return Optional.of(actPass);
    }

    @Override
    public void setCurrentPass(Passbook pass) {
        actPass = pass;
    }

    @Override
    public void setCurrentPass(Optional<Passbook> pass) {
        actPass = pass.get();
    }

    @Override
    public boolean deletePassWithId(String id) {
        return AXT.at(new File(path + "/" + id)).deleteRecursive();
    }

}
