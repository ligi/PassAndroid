package org.ligi.passandroid.model;

import android.content.Context;

import org.ligi.axt.AXT;
import org.ligi.passandroid.helper.DirectoryFileFilter;
import org.ligi.passandroid.model.comparator.PassByTimeComparator;
import org.ligi.passandroid.model.comparator.PassByTypeFirstAndTimeSecondComparator;
import org.ligi.passandroid.reader.AppleStylePassReader;
import org.ligi.passandroid.reader.PassReader;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidFileSystemPassStore implements PassStore {

    private final Context context;
    private final String path;

    private List<FiledPass> passList = new ArrayList<>();
    private Pass actPass;

    public AndroidFileSystemPassStore(final Context context,final Settings settings) {
        this.context = context;
        path = settings.getPassesDir();

        refreshPassesList();
    }

    @Override
    public void deleteCacheForId(String id) {
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

        final List<String> newIds = Arrays.asList(getPassIDArray());
        final List<String> oldIds = new ArrayList<>();
        final List<FiledPass> toRemove = new ArrayList<>();

        for (FiledPass pass : passList) {
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


    private FiledPass getCachedPassOrLoad(String id) {
        final File cachedFile = getCacheFile(id);
        try {
            return AXT.at(cachedFile).loadToObject();
        } catch (Exception ignored) {
        }

        Log.i("Passbook cache miss");
        final String language = context.getResources().getConfiguration().locale.getLanguage();

        final FiledPass pass = readPass(id, language);

        AXT.at(cachedFile).writeObject(pass);
        return pass;
    }

    private FiledPass readPass(String id, String language) {
        if (new File(getPathForID(id), "data.json").exists()) {
            return PassReader.read(getPathForID(id));
        } else {
            return AppleStylePassReader.read(getPathForID(id), language);
        }
    }

    private File getPassesDirSafely() {
        final File passes_dir = new File(path);

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
                Collections.sort(passList, new PassByTypeFirstAndTimeSecondComparator());
                break;

            case DATE:
                Collections.sort(passList, new PassByTimeComparator());
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
    public Pass getCurrentPass() {
        return actPass;
    }

    @Override
    public void setCurrentPass(final Pass pass) {
        actPass = pass;
    }

    @Override
    public boolean deletePassWithId(final String id) {
        return AXT.at(new File(getPathForID(id))).deleteRecursive();
    }

    public String getPathForID(final String id) {
        return path + "/" + id;
    }

}
