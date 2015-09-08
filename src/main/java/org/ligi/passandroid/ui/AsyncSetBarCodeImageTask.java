package org.ligi.passandroid.ui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import org.ligi.passandroid.helper.BarcodeHelper;
import org.ligi.passandroid.model.BarCode;

public class AsyncSetBarCodeImageTask extends AsyncTask<BarCode, Void, Bitmap> {

    private final ImageView view;
    private final int barcodeSize;

    public AsyncSetBarCodeImageTask(ImageView view, final int barcodeSize) {
        this.view = view;
        this.barcodeSize = barcodeSize;
    }

    @Override
    protected Bitmap doInBackground(BarCode... params) {
        return BarcodeHelper.generateBarCodeBitmap(params[0].getMessage(), params[0].getFormat(), barcodeSize);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        view.setImageBitmap(bitmap);
    }
}
