package org.ligi.ticketviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.ticketviewer.helper.BarcodeHelper;
import org.ligi.ticketviewer.helper.FileHelper;
import org.ligi.tracedroid.logging.Log;

import java.io.File;

/**
 * User: ligi
 * Date: 2/9/13
 * Time: 10:48 PM
 */
public class PassbookParser {

    private String path;

    private String problem_str = "";
    private boolean passbook_valid = true; // be positive
    private String barcode_msg;

    private Bitmap barcodeBitmap = null;
    private com.google.zxing.BarcodeFormat barcodeFormat;

    private Bitmap icon_bitmap;

    public PassbookParser(String path) {

        this.path = path;

        JSONObject pass_json = null;

        try {
            pass_json = new JSONObject(FileHelper.file2String(new File(path + "/pass.json")));
            JSONObject barcode_json = pass_json.getJSONObject("barcode");

            barcodeFormat = com.google.zxing.BarcodeFormat.QR_CODE; // DEFAULT

            barcode_msg = barcode_json.getString("message");

            if (barcode_json.getString("format").contains("417"))
                barcodeFormat = com.google.zxing.BarcodeFormat.PDF_417;

            // TODO should check a bit more with barcode here - this can be dangerous


        } catch (Exception e) {
            problem_str += "Problem with pass.json";
            passbook_valid = false;
            return;
        }

        if (pass_json != null)
            try {
                String color_str = pass_json.getString("backgroundColor");

                Log.i("parsed color:" + color_str);
            } catch (JSONException e) {
            }
    }

    public boolean isValid() {
        return passbook_valid;
    }

    public Bitmap getBarcodeBitmap() {
        if (barcodeBitmap == null) {
            barcodeBitmap = BarcodeHelper.generateBarCode(barcode_msg, barcodeFormat);
        }
        return barcodeBitmap;
    }

    public Bitmap getIconBitmap() {
        if (icon_bitmap == null && path != null) {
            icon_bitmap = BitmapFactory.decodeFile(path + "/logo@2x.png");

            if (icon_bitmap == null)
                icon_bitmap = BitmapFactory.decodeFile(path + "/logo.png");

            if (icon_bitmap == null)
                icon_bitmap = BitmapFactory.decodeFile(path + "/icon@2x.png");

            if (icon_bitmap == null)
                icon_bitmap = BitmapFactory.decodeFile(path + "/icon.png");


        }
        return icon_bitmap;
    }

}
