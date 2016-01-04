package org.ligi.passandroid.model;

import java.io.Serializable;

public interface FiledPass extends Pass, Serializable {

    String getPath();

    void setPath(String Path);

    void save(PassStore store);
}
