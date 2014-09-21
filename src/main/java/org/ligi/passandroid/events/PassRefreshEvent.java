package org.ligi.passandroid.events;

import org.ligi.passandroid.model.Pass;

public class PassRefreshEvent {
    public final Pass pass;

    public PassRefreshEvent(Pass pass) {
        this.pass = pass;
    }
}
