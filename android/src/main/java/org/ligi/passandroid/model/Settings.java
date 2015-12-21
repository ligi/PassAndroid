package org.ligi.passandroid.model;

import org.ligi.passandroid.model.comparator.PassSortOrder;

import java.io.File;

public interface Settings {

    void setSortOrder(PassSortOrder order);

    PassSortOrder getSortOrder();

    boolean doTraceDroidEmailSend();

    String getPassesDir();

    File getStateDir();

    String getShareDir();

}
