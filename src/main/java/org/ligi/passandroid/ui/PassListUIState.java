package org.ligi.passandroid.ui;

import android.content.Context;

import org.ligi.passandroid.R;

public class PassListUIState {

    public final static int UISTATE_INIT = 0;
    public final static int UISTATE_SCAN = 1;
    public final static int UISTATE_LIST = 2;

    private int state = UISTATE_INIT;

    private final Context ctx;

    public PassListUIState(Context ctx) {
        this.ctx = ctx;
    }

    public void set(int state) {
        this.state = state;
    }

    public int get() {
        return state;
    }

    public String getHtmlResForEmptyView() {
        switch (state) {
            case PassListUIState.UISTATE_INIT:
                return ctx.getString(R.string.please_wait);

            case PassListUIState.UISTATE_SCAN:
                return ctx.getString(R.string.scan_empty_text) + ctx.getString(R.string.no_passes_appendix);

            default:
                return ctx.getString(R.string.no_passes_empty_text) + ctx.getString(R.string.no_passes_appendix);
        }
    }

}
