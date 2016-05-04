package org.ligi.passandroid.ui.pass_view_holder;

import android.app.Activity;
import android.view.View;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.Pass;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CondensedPassViewHolder extends PassViewHolder {

    public CondensedPassViewHolder(View view) {
        super(view);
    }

    @Override
    public void apply(Pass pass, PassStore passStore, Activity activity) {
        super.apply(pass, passStore, activity);

        final String extraString = getExtraString();
        if (extraString != null && !extraString.isEmpty()) {
            dateOrExtraText.setText(extraString);
            dateOrExtraText.setVisibility(VISIBLE);
        } else {
            dateOrExtraText.setVisibility(GONE);
        }

        addCalendar.setText(getTimeInfoString());

    }
}
