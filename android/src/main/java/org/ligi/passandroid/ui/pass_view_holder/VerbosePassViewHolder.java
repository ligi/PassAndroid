package org.ligi.passandroid.ui.pass_view_holder;

import android.app.Activity;
import android.view.View;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.Pass;

public class VerbosePassViewHolder extends PassViewHolder {

    public VerbosePassViewHolder(View view) {
        super(view);
    }

    public void apply(final Pass pass, final PassStore passStore, final Activity activity) {
        super.apply(pass, passStore, activity);

        String stringToSetForDateOrExtraText = null;

        if (getTimeInfoString() != null) {
            stringToSetForDateOrExtraText = getTimeInfoString();
        } else if (getExtraString() != null) {
            stringToSetForDateOrExtraText = getExtraString();
        }

        if (stringToSetForDateOrExtraText != null) {
            dateOrExtraText.setText(stringToSetForDateOrExtraText);
            dateOrExtraText.setVisibility(View.VISIBLE);
        } else {
            dateOrExtraText.setVisibility(View.GONE);
        }
    }
}
