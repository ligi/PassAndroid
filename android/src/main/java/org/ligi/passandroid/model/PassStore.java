package org.ligi.passandroid.model;

import android.support.annotation.Nullable;
import android.content.Context;

import java.util.List;

public interface PassStore {

    void deleteCacheForId(String id);

    void refreshPassesList();

    @Nullable
    Pass getPassbookForId(String id);

    boolean deletePassWithId(String id);

    String getPathForID(String id);

    void preCachePassesList();

    List<FiledPass> getPassList();

    @Nullable
    Pass getCurrentPass();

    void setCurrentPass(@Nullable Pass pass);

    PassClassifier getClassifier(Context context);
}
