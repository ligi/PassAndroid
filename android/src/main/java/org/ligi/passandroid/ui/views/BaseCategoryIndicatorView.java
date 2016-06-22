package org.ligi.passandroid.ui.views;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.pass.PassType;

public class BaseCategoryIndicatorView extends LinearLayout {

    private ImageView topImageView;

    public BaseCategoryIndicatorView(Context context, AttributeSet attrs) {
        this(context,attrs,R.layout.category_indicator_base);
    }

    public BaseCategoryIndicatorView(Context context, AttributeSet attrs, @LayoutRes final int layout) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(layout, this, true);
        topImageView = ButterKnife.findById(this,R.id.topImageView);
    }

    public void setImageByCategory(PassType category) {
        if (category == null) {
            topImageView.setVisibility(View.GONE);
        } else {
            topImageView.setVisibility(View.VISIBLE);
            topImageView.setImageResource(CategoryHelper.INSTANCE.getCategoryTopImageRes(category));
        }
    }

    public void setAccentColor(int color) {
        setBackgroundColor(color);
    }

}
