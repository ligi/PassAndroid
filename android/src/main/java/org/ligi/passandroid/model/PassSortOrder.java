package org.ligi.passandroid.model;

public enum PassSortOrder {
    DATE(0),
    TYPE(1);

    private final int i;

    PassSortOrder(int i) {
        this.i = i;
    }

    public int getInt() {
        return i;
    }

}
