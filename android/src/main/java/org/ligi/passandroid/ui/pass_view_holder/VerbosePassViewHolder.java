package org.ligi.passandroid.ui.pass_view_holder;

import android.app.Activity;
import android.view.View;

import org.ligi.passandroid.model.Pass;

import static android.view.View.GONE;

public class VerbosePassViewHolder extends PassViewHolder {


    public VerbosePassViewHolder(View view) {
        super(view);
    }

    public void apply(final Pass pass, final Activity activity) {
        super.apply(pass, activity);

        if (getTimeInfoString() != null) {
            dateOrExtraText.setText(getTimeInfoString());
        } else if (getExtraString() != null) {
            dateOrExtraText.setText(getExtraString());
        } else {
            dateOrExtraText.setVisibility(GONE);
        }

    }


}
