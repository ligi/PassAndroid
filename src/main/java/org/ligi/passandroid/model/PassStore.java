package org.ligi.passandroid.model;

import com.google.common.base.Optional;

import java.util.List;

public interface PassStore {

    public enum SortOrder {
        DATE_ASC(0),
        DATE_DESC(2),
        TYPE(1);

        private final int i;

        SortOrder(int i) {
            this.i = i;
        }

        public int getInt() {
            return i;
        }

    }

    public class CountedType implements Comparable<CountedType> {
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

    public void deleteCache();

    public void refreshPassesList();

    public int passCount();

    public boolean isEmpty();

    public Pass getPassbookAt(int pos);

    public Pass getPassbookForId(String id,String language);

    public void sort(SortOrder order);

    public List<CountedType> getCountedTypes();

    public Optional<Pass> getCurrentPass();

    public void setCurrentPass(Pass pass);

    public void setCurrentPass(Optional<Pass> pass);

    public boolean deletePassWithId(String id);
}
