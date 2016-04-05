package org.ligi.passandroid.ui.edit_fragments;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.io.File;
import java.io.IOException;
import org.ligi.axt.AXT;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.helper.Files;
import org.ligi.passandroid.model.pass.PassImpl;
import static android.app.Activity.RESULT_OK;

public class ImageEditFragment extends PassandroidFragment {

    private static final int REQ_CODE_PICK_LOGO = 1;
    private static final int REQ_CODE_PICK_ICON = 2;
    private static final int REQ_CODE_PICK_STRIP = 3;
    private static final int REQ_CODE_PICK_THUMBNAIL = 4;

    @OnClick(R.id.pickIcon)
    public void onPickIcon() {
        startPick(REQ_CODE_PICK_ICON);
    }

    @OnClick(R.id.pickLogo)
    public void onPickLogo() {
        startPick(REQ_CODE_PICK_LOGO);
    }

    @OnClick(R.id.pickStrip)
    public void onPickStrip() {
        startPick(REQ_CODE_PICK_STRIP);
    }

    @OnClick(R.id.pickThumbnail)
    public void onPickThumbnail() {
        startPick(REQ_CODE_PICK_THUMBNAIL);
    }

    private void startPick(int reqCodePickLogo) {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), reqCodePickLogo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.edit_images, container, false);
        ButterKnife.bind(this, inflate);

        return inflate;
    }

    private void refresh() {
        bus.post(new PassRefreshEvent(getPass()));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            extractImage(imageReturnedIntent, getImageStringByRequestCode(requestCode));
            refresh();
        }

    }

    private static String getImageStringByRequestCode(final int requestCode) {
        switch (requestCode) {
            case REQ_CODE_PICK_LOGO:
                return "logo";
            case REQ_CODE_PICK_ICON:
                return "icon";
            case REQ_CODE_PICK_THUMBNAIL:
                return "thumbnail";
            case REQ_CODE_PICK_STRIP:
                return "strip";
        }
        throw new IllegalArgumentException("unknown request code " + requestCode);
    }

    private void extractImage(Intent imageReturnedIntent, String name) {
        final String getPath = getPath(getContext(), imageReturnedIntent.getData());
        final File extract = new File(getPath);
        try {
            Files.copy(extract, new File(passStore.getPathForID(getPass().getId()), name + PassImpl.Companion.getFILETYPE_IMAGES()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        String result = null;
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            result = handleKitKat(context, uri);
        }
        // MediaStore (and general)
        if (result == null && "content".equalsIgnoreCase(uri.getScheme())) {
            result = getDataColumn(context, uri, null, null);
        }
        // File
        if (result == null && "file".equalsIgnoreCase(uri.getScheme())) {
            result = uri.getPath();
        }

        if (result == null) {
            final File file = AXT.at(uri).loadImage(context);
            if (file != null) {
                result = file.getAbsolutePath();
            }
        }

        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Nullable
    public static String handleKitKat(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
