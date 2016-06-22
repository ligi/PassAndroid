package org.ligi.passandroid.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import butterknife.ButterKnife;
import org.ligi.passandroid.R;

public class CategoryIndicatorViewWithIcon extends BaseCategoryIndicatorView {

    private ImageView iconImageView;

    public CategoryIndicatorViewWithIcon(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.category_indicator);
        iconImageView = ButterKnife.findById(this, R.id.iconImageView);
    }

    public void setIcon(final Bitmap iconBitmap) {
        iconImageView.setImageBitmap(iconBitmap);
    }

}
