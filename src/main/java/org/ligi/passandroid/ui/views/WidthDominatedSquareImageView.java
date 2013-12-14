package org.ligi.passandroid.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class WidthDominatedSquareImageView extends ImageView {


    public WidthDominatedSquareImageView(Context context) {
        super(context);
    }

    public WidthDominatedSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);

        this.setMeasuredDimension(parentWidth, parentWidth);
    }
}
