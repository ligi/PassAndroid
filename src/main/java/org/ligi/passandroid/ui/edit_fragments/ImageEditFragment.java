package org.ligi.passandroid.ui.edit_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.io.Files;

import org.ligi.passandroid.App;
import org.ligi.passandroid.ImageFromIntentUriToFileConverter;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.model.PassImpl;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class ImageEditFragment extends Fragment {

    private static final int REQ_CODE_PICK_LOGO = 1;
    private static final int REQ_CODE_PICK_ICON = 2;
    private static final int REQ_CODE_PICK_STRIP = 3;
    private static final int REQ_CODE_PICK_THUMBNAIL = 4;

    private static PassImpl getPass() {
        return (PassImpl) App.getPassStore().getCurrentPass().get();
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
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), reqCodePickLogo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.edit_images, container, false);
        ButterKnife.inject(this, inflate);

        return inflate;
    }

    private void refresh() {
        App.getBus().post(new PassRefreshEvent(getPass()));
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent imageReturnedIntent) {
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
        final File extract = new ImageFromIntentUriToFileConverter(getActivity()).extract(imageReturnedIntent.getData());
        try {
            Files.copy(extract, new File(getPass().getPath() + "/" + name + PassImpl.FILETYPE_IMAGES));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
