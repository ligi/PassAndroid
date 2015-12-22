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
}
