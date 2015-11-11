package org.ligi.passandroid.ui;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.ligi.passandroid.helper.BarcodeHelper;
import org.ligi.passandroid.model.BarCode;

public class AsyncSetBarCodeImageTask extends AsyncTask<BarCode, Void, BitmapDrawable> {

    private final ImageView view;

    private Resources resources;

    public AsyncSetBarCodeImageTask(ImageView view) {
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        resources = view.getContext().getResources();
    }

    @Override
    protected BitmapDrawable doInBackground(BarCode... params) {
        return BarcodeHelper.generateBitmapDrawable(resources, params[0].getMessage(), params[0].getFormat());
    }

    @Override
    protected void onPostExecute(BitmapDrawable bitmap) {
        view.setImageDrawable(bitmap);
    }
}
