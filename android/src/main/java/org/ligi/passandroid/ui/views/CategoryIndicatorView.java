package org.ligi.passandroid.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.CategoryHelper;

import butterknife.ButterKnife;

public class CategoryIndicatorView extends LinearLayout {


    @Bind(R.id.categoryExtraText)
    TextView extraText;

    @Bind(R.id.topImageView)
    ImageView topImageView;

    public CategoryIndicatorView(Context context) {
        super(context);
    }

    public CategoryIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.category_indicator, this, true);
        ButterKnife.bind(this);
    }

    public void setImageByCategory(String category) {
        if (category == null) {
            topImageView.setVisibility(View.GONE);
        } else {
            topImageView.setVisibility(View.VISIBLE);
            topImageView.setImageResource(CategoryHelper.getCategoryTopImageRes(category));
        }
    }

    public void setExtraTextToCatShortString(String category) {
        extraText.setText(CategoryHelper.getCategoryShortStr(category));
    }

    public void setExtraText(String text) {
        extraText.setText(text);
    }

    public void setTextBackgroundColor(int color) {
        setBackgroundColor(color);
    }

    public void setTextColor(int color) {
        extraText.setTextColor(color);
    }

    public void setTextSize(int unit, float size) {
        extraText.setTextSize(unit, size);
    }
}
