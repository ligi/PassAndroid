package org.ligi.passandroid.model;

import android.support.annotation.Nullable;

import org.ligi.passandroid.model.comparator.PassSortOrder;

import java.util.List;

public interface PassStore {

    boolean deletePassWithId(String id);

    String getPathForID(String id);


    List<FiledPass> getPassList();

    void preCachePassesList();

    void deleteCacheForId(String id);

    void refreshPassesList();

    Pass getPassbookForId(String id);


    void sort(PassSortOrder order);

    @Nullable
    Pass getCurrentPass();

    void setCurrentPass(@Nullable Pass pass);

}
