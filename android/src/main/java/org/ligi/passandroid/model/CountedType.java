package org.ligi.passandroid.model;

import android.support.annotation.NonNull;

public class CountedType implements Comparable<CountedType> {
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CountedType)) {
            return false;
        }

        CountedType other = (CountedType) o;

        return type.equals(other.type) && count.equals(other.count);
    }
}
