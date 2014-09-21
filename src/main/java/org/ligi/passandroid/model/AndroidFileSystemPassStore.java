package org.ligi.passandroid.model;

import android.content.Context;

import com.google.common.base.Optional;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.helper.DirectoryFileFilter;
import org.ligi.passandroid.reader.AppleStylePassReader;
import org.ligi.passandroid.reader.PassReader;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidFileSystemPassStore implements PassStore {

    private final Context context;
    private String path;

    private List<Pass> passList = new ArrayList<>();
    private Pass actPass;

    public AndroidFileSystemPassStore(Context context) {
        this.context = context;
        refreshPassesList();
    }

    @Override
    public void deleteCacheForId(String id) {
        getCacheFile(id).delete();
    }

    @Override
    public void deleteCache() {
        for (String id : getPassIDArray()) {
            deleteCache(id);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteCache(String id) {
        getCacheFile(id).delete();
    }

    private File getCacheFile(String id) {
        return new File(getPathForID(id) + "/base_cache.obj");
    }

    @Override
    public void preCachePassesList() {
        for (String key : getPassIDArray()) {
            getPassbookForId(key);
        }
    }

    @Override
    public void refreshPassesList() {
        path = App.getPassesDir(context);

        final List<String> newIds = Arrays.asList(getPassIDArray());
        final List<String> oldIds = new ArrayList<>();
        final List<Pass> toRemove = new ArrayList<>();

        for (Pass pass : passList) {
            oldIds.add(pass.getId());
            if (!newIds.contains(pass.getId())) {
                toRemove.add(pass);
            }
        }

        for (String newId : newIds) {
            if (!oldIds.contains(newId)) {
                passList.add(getCachedPassOrLoad(newId));
            }
        }

        passList.removeAll(toRemove);
    }

    private String[] getPassIDArray() {
        return getPassesDirSafely().list(new DirectoryFileFilter());
    }


    private Pass getCachedPassOrLoad(String id) {
        final File cachedFile = getCacheFile(id);
        try {
            return AXT.at(cachedFile).loadToObject();
        } catch (Exception e) {
            //noinspection EmptyCatchBlock
        }

        Log.i("Passbook cache miss");
        final String language = context.getResources().getConfiguration().locale.getLanguage();

        final Pass pass = readPass(id, language);

        AXT.at(cachedFile).writeObject(pass);
        return pass;
    }

    private Pass readPass(String id, String language) {
        Pass pass;
        if (new File(getPathForID(id), "data.json").exists()) {
            pass = PassReader.read(getPathForID(id));
        } else {
            pass = AppleStylePassReader.read(getPathForID(id), language);
        }
        return pass;
    }

    private File getPassesDirSafely() {
        final File passes_dir = new File(App.getPassesDir(context));

        if (!passes_dir.exists()) {
            passes_dir.mkdirs();
        }

        return passes_dir;
    }

    @Override
    public int passCount() {
        return passList.size();
    }

    @Override
    public boolean isEmpty() {
        return passList.isEmpty();
    }

    @Override
    public Pass getPassbookAt(final int pos) {
        return passList.get(pos);
    }

    @Override
    public Pass getPassbookForId(final String id) {
        for (Pass pass : passList) {
            if (pass.getId().equals(id)) {
                return pass;
            }
        }

        return getCachedPassOrLoad(id);
    }

    @Override
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

                        if (!lhs.getRelevantDate().isPresent() && !rhs.getRelevantDate().isPresent()) {
                            return 0;
                        }

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

    @Override
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

    public String getPathForID(final String id) {
        return path + "/" + id;
    }
}
