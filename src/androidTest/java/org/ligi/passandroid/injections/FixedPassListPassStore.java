package org.ligi.passandroid.injections;

import com.google.common.base.Optional;

import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Passbook;
import org.ligi.passandroid.model.ReducedPassInformation;

import java.util.ArrayList;
import java.util.List;

public class FixedPassListPassStore implements PassStore {

    private final List<Passbook> passes;
    private Passbook actPass;

    public FixedPassListPassStore(List<Passbook> passes) {
        this.passes = passes;
    }

    @Override
    public void deleteCache() {
        // no effect in this impl
    }

    @Override
    public void refreshPassesList() {
        // no effect in this impl
    }

    @Override
    public int passCount() {
        return passes.size();
    }

    @Override
    public boolean isEmpty() {
        return passCount() == 0;
    }

    @Override
    public Passbook getPassbookAt(int pos) {
        return passes.get(pos);
    }

    @Override
    public Passbook getPassbookForId(String id) {
        for (Passbook pass : passes) {
            if (pass.getId().equals(id)) {
                return pass;
            }
        }
        return null;
    }

    @Override
    public ReducedPassInformation getReducedPassbookAt(int pos) {
        return new ReducedPassInformation(getPassbookAt(pos));
    }

    @Override
    public void sort(SortOrder order) {
    }

    @Override
    public List<CountedType> getCountedTypes() {
        return new ArrayList<>();
    }

    @Override
    public Optional<Passbook> getCurrentPass() {
        return Optional.of(actPass);
    }

    @Override
    public void setCurrentPass(Passbook pass) {
        actPass = pass;
    }

    @Override
    public void setCurrentPass(Optional<Passbook> pass) {
        actPass = pass.get();
    }

    @Override
    public boolean deletePassWithId(String id) {
        return false;
    }
}
