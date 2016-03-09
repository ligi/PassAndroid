package org.ligi.passandroid.model;

import org.ligi.passandroid.model.comparator.PassSortOrder;

import java.io.File;

public interface Settings {

    PassSortOrder getSortOrder();

    boolean doTraceDroidEmailSend();

    String getPassesDir();

    File getStateDir();

    String getShareDir();

    boolean isCondensedModeEnabled();

    boolean isAutomaticLightEnabled();

}
