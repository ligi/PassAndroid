package org.ligi.passandroid.events;

import org.ligi.passandroid.model.Pass;

import java.util.List;

public class ScanFinishedEvent {

    public final List<Pass> foundPasses;

    public ScanFinishedEvent(final List<Pass> foundPasses) {
        this.foundPasses = foundPasses;
    }
}
