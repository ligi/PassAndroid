package org.ligi.passandroid.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;
import org.ligi.passandroid.R;

public class MyShyFABBehavior extends CoordinatorLayout.Behavior<FloatingActionsMenu> {

    public MyShyFABBehavior() {
    }

    public MyShyFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionsMenu child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout || dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionsMenu child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            updateFabTranslationForSnackbar(child, dependency);
        }
        if (dependency instanceof AppBarLayout) {
            final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            final int fabBottomMargin = lp.bottomMargin;
            final int distanceToScroll;
            if (child.isExpanded()) {
                distanceToScroll = child.getHeight() + fabBottomMargin;
            } else {
                distanceToScroll = (int) (child.getContext().getResources().getDimension(R.dimen.fab_size_normal) + 2 * fabBottomMargin);
            }
            final float ratio = ViewCompat.getY(dependency) / getToolbarHeight(dependency.getContext());

            ViewCompat.setTranslationY(child, -distanceToScroll * ratio);
        }
        return false;
    }

    @Override
    public void onDependentViewRemoved(final CoordinatorLayout parent, final FloatingActionsMenu child, final View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        onDependentViewChanged(parent,child,dependency);
    }

    private void updateFabTranslationForSnackbar(FloatingActionsMenu child, View dependency) {
        final float translationY = ViewCompat.getTranslationY(dependency) - dependency.getHeight();
        final float translationYClipped = Math.min(0, translationY);
        ViewCompat.setTranslationY(child, translationYClipped);
    }

    private int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
}
