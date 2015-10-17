package org.ligi.passandroid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class GoldenCutLinearLayout extends LinearLayout {

    public GoldenCutLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode==MeasureSpec.EXACTLY) {
            heightMeasureSpec=MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.EXACTLY);
        } else if (heightMode==MeasureSpec.EXACTLY) {
            widthMeasureSpec=MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec),MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

    }
}
