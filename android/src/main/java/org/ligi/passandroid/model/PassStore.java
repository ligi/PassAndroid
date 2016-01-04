package org.ligi.passandroid.model;

import android.support.annotation.Nullable;

import java.util.List;

public interface PassStore {

    List<FiledPass> getPassList();

    void preCachePassesList();

    void deleteCacheForId(String id);

    void refreshPassesList();

    Pass getPassbookForId(String id);

    void sort(PassSortOrder order);

    @Nullable
    Pass getCurrentPass();

    void setCurrentPass(@Nullable Pass pass);

    boolean deletePassWithId(String id);

    String getPathForID(String id);
}
