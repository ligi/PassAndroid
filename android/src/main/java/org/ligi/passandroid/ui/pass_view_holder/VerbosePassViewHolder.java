package org.ligi.passandroid.ui.pass_view_holder;

import android.app.Activity;
import android.view.View;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassStore;
import static android.view.View.GONE;

public class VerbosePassViewHolder extends PassViewHolder {


    public VerbosePassViewHolder(View view) {
        super(view);
    }

    public void apply(final Pass pass, final PassStore passStore, final Activity activity) {
        super.apply(pass, passStore, activity);

        if (getTimeInfoString() != null) {
            dateOrExtraText.setText(getTimeInfoString());
        } else if (getExtraString() != null) {
            dateOrExtraText.setText(getExtraString());
        } else {
            dateOrExtraText.setVisibility(GONE);
        }

    }


}
