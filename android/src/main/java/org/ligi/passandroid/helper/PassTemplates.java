package org.ligi.passandroid.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.PassBitmapDefinitions;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.model.pass.PassField;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.model.pass.PassType;

public class PassTemplates {

    public static final String APP = "passandroid";

    public static Pass createAndAddEmptyPass(final PassStore passStore, final Resources resources) {
        final PassImpl pass = new PassImpl(UUID.randomUUID().toString());
        pass.setAccentColor(0xFF0000ff);
        pass.setDescription("custom Pass");
        pass.setApp(APP);
        pass.setType(PassType.EVENT);

        passStore.setCurrentPass(pass);
        passStore.save(pass);

        final Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher);

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(new File(passStore.getPathForID(pass.getId()), PassBitmapDefinitions.BITMAP_ICON+".png")));
        } catch (FileNotFoundException ignored) {
        }

        return pass;
    }

    public static Pass createPassForImageImport() {
        final PassImpl pass = new PassImpl(UUID.randomUUID().toString());
        pass.setAccentColor(0xFF0000ff);
        pass.setDescription("image import");

        List<PassField> list = new ArrayList<>();

        list.add(new PassField(null, "source", "image", false));
        list.add(new PassField(null, "note", "This is imported from image - not a real pass", false));
        pass.setFields(list);
        pass.setApp(APP);
        pass.setType(PassType.EVENT);
        return pass;
    }
}
