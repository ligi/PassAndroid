package org.ligi.passandroid.model;

import android.support.annotation.Nullable;

import java.util.Set;

public interface PassStore {

    void preCachePassesList();

    void deleteCacheForId(String id);

    void refreshPassesList();

    int passCount();

    Pass getPassbookAt(int pos);

    Pass getPassbookForId(String id);

    void sort(PassSortOrder order);

    Set<CountedType> getCountedTypes();

    @Nullable
    Pass getCurrentPass();

    void setCurrentPass(@Nullable Pass pass);

    boolean deletePassWithId(String id);

    String getPathForID(String id);
}
