package org.ligi.passandroid.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.pass.PassType;

public class CategoryIndicatorView extends LinearLayout {

    private ImageView topImageView;

    public CategoryIndicatorView(Context context) {
        super(context);
    }

    public CategoryIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.category_indicator, this, true);
        topImageView = ButterKnife.findById(this,R.id.topImageView);
    }

    public void setImageByCategory(PassType category) {
        if (category == null) {
            topImageView.setVisibility(View.GONE);
        } else {
            topImageView.setVisibility(View.VISIBLE);
            topImageView.setImageResource(CategoryHelper.getCategoryTopImageRes(category));
        }
    }

    public void setAccentColor(int color) {
        setBackgroundColor(color);
    }

}
