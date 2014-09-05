package org.ligi.passandroid.model;

import android.content.Context;

import com.google.common.base.Optional;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
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

    private List<Pass> passList;
    private Pass actPass;

    public AndroidFileSystemPassStore(Context context) {
        this.context = context;
        refreshPassesList();
    }

    public void deleteCache() {
        for (String id : getPassIDArray()) {
            getCacheFile(id).delete();
        }
    }

    private File getCacheFile(String id) {
        return new File(getPathForID(id) + "/base_cache.obj");
    }

    public void refreshPassesList() {
        path = App.getPassesDir(context);

        passList = new ArrayList<>();

        for (String id : getPassIDArray()) {
            passList.add(getCachedPassOrLoad(id));
        }

    }

    private String[] getPassIDArray() {
        return getPassesDirSafely().list(new DirectoryFileFilter());
    }

    private Pass getCachedPassOrLoad(String id) {
        final File cachedFile = getCacheFile(id);
        try {
            return AXT.at(cachedFile).loadToObject();
        } catch (Exception e) {
        }

        final String language = context.getResources().getConfiguration().locale.getLanguage();
        final Pass pass = AppleStylePassReader.read(getPathForID(id), language);
        AXT.at(cachedFile).writeObject(pass);
        return pass;
    }

    private File getPassesDirSafely() {
        final File passes_dir = new File(App.getPassesDir(context));

        if (!passes_dir.exists()) {
            passes_dir.mkdirs();
        }
        return passes_dir;
    }

    public int passCount() {
        return passList.size();
    }

    public boolean isEmpty() {
        return passList.isEmpty();
    }

    public Pass getPassbookAt(final int pos) {
        return passList.get(pos);
    }

    public Pass getPassbookForId(final String id,final String language) {
        final String mPath = path + "/" + id;
        // TODO read from cache
        return AppleStylePassReader.read(mPath,language);
    }

    public void sort(final SortOrder order) {
        switch (order) {
            case TYPE:
                Collections.sort(passList, new Comparator<Pass>() {
                    @Override
                    public int compare(Pass lhs, Pass rhs) {
                        if (lhs.getType() == rhs.getType()) { // that looks bad but makes sense for both being null
                            return 0;
                        }

                        if (lhs.getType() == null) {
                            return 1;
                        }
                        if (rhs.getType() == null) {
                            return -1;
                        }
                        return lhs.getType().compareTo(rhs.getType());
                    }
                });
                break;

            case DATE:
                Collections.sort(passList, new Comparator<Pass>() {
                    @Override
                    public int compare(Pass lhs, Pass rhs) {

                        if (!lhs.getRelevantDate().isPresent()) {
                            return 1;
                        }
                        if (!rhs.getRelevantDate().isPresent()) {
                            return -1;
                        }
                        return rhs.getRelevantDate().get().compareTo(lhs.getRelevantDate().get());
                    }
                });
                break;
        }

    }

    public List<CountedType> getCountedTypes() {
        // TODO - some sort of caching
        final Map<String, Integer> tempMap = new HashMap<>();

        for (Pass info : passList) {
            if (tempMap.containsKey(info.getTypeNotNull())) {
                final Integer i = tempMap.get(info.getTypeNotNull());
                tempMap.put(info.getTypeNotNull(), i + 1);
            } else {
                tempMap.put(info.getTypeNotNull(), 1);
            }
        }

        final List<CountedType> result = new ArrayList<>();

        for (String type : tempMap.keySet()) {
            result.add(new CountedType(type, tempMap.get(type)));
        }

        Collections.sort(result);

        return result;
    }

    @Override
    public Optional<Pass> getCurrentPass() {
        return Optional.fromNullable(actPass);
    }

    @Override
    public void setCurrentPass(final Pass pass) {
        actPass = pass;
    }

    @Override
    public void setCurrentPass(final Optional<Pass> pass) {
        actPass = pass.get();
    }

    @Override
    public boolean deletePassWithId(final String id) {
        return AXT.at(new File(getPathForID(id))).deleteRecursive();
    }

    private String getPathForID(final String id) {
        return path + "/" + id;
    }
}
