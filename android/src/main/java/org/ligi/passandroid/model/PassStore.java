package org.ligi.passandroid.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Set;

public interface PassStore {

    enum SortOrder {
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

    class CountedType implements Comparable<CountedType> {
        public final String type;
        public final Integer count;

        public CountedType(String type, Integer count) {
            this.type = type;
            this.count = count;
        }

        @Override
        public int compareTo(@NonNull CountedType another) {
            return another.count - count;
        }
    }

    void preCachePassesList();

    void deleteCacheForId(String id);

    void refreshPassesList();

    int passCount();

    Pass getPassbookAt(int pos);

    Pass getPassbookForId(String id);

    void sort(SortOrder order);

    Set<CountedType> getCountedTypes();

    @Nullable
    Pass getCurrentPass();

    void setCurrentPass(@Nullable Pass pass);

    boolean deletePassWithId(String id);

    String getPathForID(String id);
}
