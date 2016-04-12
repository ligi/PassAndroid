package org.ligi.passandroid.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.ligi.passandroid.R;

public class PassViewHelper {

    private final Activity context;

    public PassViewHelper(final Activity context) {
        this.context = context;
    }

    public int getFingerSize() {
        return context.getResources().getDimensionPixelSize(R.dimen.finger);
    }

    public void setBitmapSafe(ImageView imageView, Bitmap bitmap) {

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            imageView.setLayoutParams(getLayoutParamsSoThatWeHaveMinimumAFingerInHeight(imageView, bitmap));
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    @NonNull
    public ViewGroup.LayoutParams getLayoutParamsSoThatWeHaveMinimumAFingerInHeight(final ImageView imageView, final Bitmap bitmap) {
        final int halfAFingerInPixels = getFingerSize();
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (bitmap.getHeight() < halfAFingerInPixels) {
            params.height = halfAFingerInPixels;
        } else {
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        return params;
    }

    public int getWindowWidth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final Point point = new Point();
            context.getWindowManager().getDefaultDisplay().getSize(point);
            return point.x;
        } else {
            return context.getWindowManager().getDefaultDisplay().getWidth();
        }
    }
}
