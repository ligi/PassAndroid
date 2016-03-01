package org.ligi.passandroid.model;

import android.support.v7.app.AppCompatDelegate;
import java.io.File;
import org.ligi.passandroid.model.comparator.PassSortOrder;

public interface Settings {

    PassSortOrder getSortOrder();

    boolean doTraceDroidEmailSend();

    File getPassesDir();

    File getStateDir();

    boolean isCondensedModeEnabled();

    boolean isAutomaticLightEnabled();

    @AppCompatDelegate.NightMode
    int getNightMode();
}
