package org.ligi.passandroid.ui;

import android.content.Context;
import android.content.res.TypedArray;
import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;
import org.ligi.passandroid.R;

@SuppressWarnings("WeakerAccess")
public class MyShyFABBehavior extends CoordinatorLayout.Behavior<FloatingActionsMenu> {

    public MyShyFABBehavior() {}

    public MyShyFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent,
                                   @NonNull FloatingActionsMenu child,
                                   @NonNull View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout || dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent,
                                          @NonNull FloatingActionsMenu child,
                                          @NonNull View dependency) {
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
            final float ratio = dependency.getY() / getToolbarHeight(dependency.getContext());

            child.setTranslationY(-distanceToScroll * ratio);
        }
        return false;
    }

    @Override
    public void onDependentViewRemoved(@NonNull final CoordinatorLayout parent,
                                       @NonNull final FloatingActionsMenu child,
                                       @NonNull final View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        onDependentViewChanged(parent,child,dependency);
    }

    private void updateFabTranslationForSnackbar(FloatingActionsMenu child, View dependency) {
        final float translationY = dependency.getTranslationY() - dependency.getHeight();
        child.setTranslationY(Math.min(0, translationY));
    }

    private int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
}
