package org.ligi.passandroid.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.CategoryHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CategoryIndicatorView extends LinearLayout {

    @InjectView(R.id.categoryExtraText)
    TextView extraText;

    @InjectView(R.id.categoryTopImage)
    ImageView topImage;

    public CategoryIndicatorView(Context context) {
        super(context);
    }

    public CategoryIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.category_indicator, this, true);
        ButterKnife.inject(this);
    }

    public void setImageByCategory(String category) {
        if (category == null) {
            topImage.setImageResource(R.drawable.category_none);
        } else {
            topImage.setImageResource(CategoryHelper.getCategoryTopImageRes(category));
        }
    }

    public void setExtraTextToCatShortString(String category) {
        extraText.setText(CategoryHelper.getCategoryShortStr(category));
    }

    public void setExtraText(String text) {
        extraText.setText(text);
    }

    public void setTextBackgroundColor(int color) {
        extraText.setBackgroundColor(color);
    }

    public void setTextColor(int color) {
        extraText.setTextColor(color);
    }

    public void setTextSize(int unit, float size) {
        extraText.setTextSize(unit, size);
    }
}
