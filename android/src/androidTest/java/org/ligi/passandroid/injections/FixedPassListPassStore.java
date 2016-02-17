package org.ligi.passandroid.injections;

import android.support.annotation.Nullable;

import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassClassifier;
import org.ligi.passandroid.model.PassStore;

import java.util.HashMap;
import java.util.List;

public class FixedPassListPassStore implements PassStore {

    private final List<FiledPass> passes;
    private Pass actPass;
    private PassClassifier passClassifier;

    public FixedPassListPassStore(List<FiledPass> passes) {
        this.passes = passes;
    }

    @Override
    public List<FiledPass> getPassList() {
        return passes;
    }

    @Override
    public void preCachePassesList() {
        // no effect in this impl
    }

    @Override
    public void deleteCacheForId(String id) {
        // no effect in this impl
    }

    @Override
    public void refreshPassesList() {
        // no effect in this impl
    }

    @Override
    @Nullable
    public Pass getPassbookForId(String id) {
        for (Pass pass : passes) {
            if (pass.getId().equals(id)) {
                return pass;
            }
        }
        return null;
    }

    @Override
    public Pass getCurrentPass() {
        return actPass;
    }

    @Override
    public void setCurrentPass(Pass pass) {
        actPass = pass;
    }

    @Override
    public PassClassifier getClassifier() {
        if (passClassifier == null) {
            passClassifier = new PassClassifier(new HashMap<String, String>(), this);
        }
        return passClassifier;
    }

    @Override
    public boolean deletePassWithId(String id) {
        return false;
    }

    @Override
    public String getPathForID(String id) {
        return null;
    }
}
