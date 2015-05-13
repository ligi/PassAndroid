package org.ligi.passandroid.model;

import com.google.common.base.Optional;
import java.util.List;

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
        public int compareTo(CountedType another) {
            return another.count - count;
        }
    }

    void preCachePassesList();

    void deleteCacheForId(String id);

    void deleteCache();

    void deleteCache(String id);

    void refreshPassesList();

    int passCount();

    boolean isEmpty();

    Pass getPassbookAt(int pos);

    Pass getPassbookForId(String id);

    void sort(SortOrder order);

    List<CountedType> getCountedTypes();

    Optional<Pass> getCurrentPass();

    void setCurrentPass(Pass pass);

    void setCurrentPass(Optional<Pass> pass);

    boolean deletePassWithId(String id);

    String getPathForID(String id);
}
