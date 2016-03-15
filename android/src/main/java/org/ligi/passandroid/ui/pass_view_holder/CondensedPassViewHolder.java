package org.ligi.passandroid.ui.pass_view_holder;

import android.app.Activity;
import android.view.View;

import org.ligi.passandroid.model.Pass;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CondensedPassViewHolder extends PassViewHolder {

    public CondensedPassViewHolder(View view) {
        super(view);
    }

    @Override
    public void apply(Pass pass, Activity activity) {
        super.apply(pass, activity);

        final String extraString = getExtraString();
        if (extraString != null) {
            dateOrExtraText.setText(extraString);
            dateOrExtraText.setVisibility(VISIBLE);
        } else {
            dateOrExtraText.setVisibility(GONE);
        }

        addCalendar.setText(getTimeInfoString());

    }
}
