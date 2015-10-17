package org.ligi.passandroid.helper;

import android.support.annotation.Nullable;

public class Strings {
    public static String nullToEmpty(@Nullable String in) {
        return in != null ? in : "";
    }
}
