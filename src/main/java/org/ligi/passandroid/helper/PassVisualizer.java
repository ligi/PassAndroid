package org.ligi.passandroid.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Optional;

import org.joda.time.DateTime;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassField;
import org.ligi.passandroid.model.PassFieldList;
import org.ligi.passandroid.ui.NavigateToLocationsDialog;
import org.ligi.passandroid.ui.PassViewHolder;
import org.ligi.passandroid.ui.views.CategoryIndicatorView;

import static butterknife.ButterKnife.findById;

public class PassVisualizer {
    public static void visualize(final Activity activity, final Pass pass, final View container) {
        new PassViewHolder(container.findViewById(R.id.pass_card)).apply(pass,activity);
    }

    public static String getFieldListAsString(PassFieldList fieldList) {
        final StringBuilder result = new StringBuilder();
        for (PassField f : fieldList) {
            result.append("<b>" + f.label + "</b>: " + f.value + "<br/>");
        }
        return result.toString();
    }
}
