package org.ligi.passandroid.model;

import java.io.Serializable;
public interface FiledPass extends Pass,Serializable {

    public String getPath();
    public void setPath(String Path);
    public void save(PassStore store);
}
