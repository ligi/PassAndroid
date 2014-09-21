package org.ligi.passandroid.ui.edit_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ligi.passandroid.App;
import org.ligi.passandroid.ImageFromIntentUriExtractor;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.model.PassImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class ImageEditFragment extends Fragment  {


    private static final int REQ_CODE_PICK_LOGO = 1;
    private static final int REQ_CODE_PICK_ICON = 2;
    private static final int REQ_CODE_PICK_STRIP = 3;
    private static final int REQ_CODE_PICK_THUMBNAIL = 4;

    private final PassImpl pass;

    public ImageEditFragment() {
        pass = (PassImpl) App.getPassStore().getCurrentPass().get();

    }

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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), reqCodePickLogo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.edit_images, null);
        ButterKnife.inject(this, inflate);

        return inflate;
    }

    private void refresh() {
        App.getBus().post(new PassRefreshEvent(pass));
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_PICK_LOGO:
                    extractImage(imageReturnedIntent, "logo");
                    pass.setLogoBitmapFile("logo.png");
                    break;
                case REQ_CODE_PICK_ICON:
                    extractImage(imageReturnedIntent, "icon");
                    pass.setIconBitmapFile("icon.png");
                    break;
                case REQ_CODE_PICK_THUMBNAIL:
                    extractImage(imageReturnedIntent, "thumbnail");
                    pass.setThumbnailBitmapFile("thumbnail.png");
                    break;
                case REQ_CODE_PICK_STRIP:
                    extractImage(imageReturnedIntent, "strip");
                    pass.setStripBitmapFile("strip.png");
                    break;
            }
            refresh();
        }


    }

    private void extractImage(Intent imageReturnedIntent, String name) {
        final File extract = new ImageFromIntentUriExtractor(getActivity()).extract(imageReturnedIntent.getData());
        try {
            copy(extract, new File(pass.getPath() + "/" + name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
