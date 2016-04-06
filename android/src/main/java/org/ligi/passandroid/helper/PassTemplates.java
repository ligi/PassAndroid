package org.ligi.passandroid.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.model.pass.PassField;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.model.pass.PassType;

public class PassTemplates {

    public static final String APP = "passandroid";

    public static Pass createEmptyPass() {
        final PassImpl pass = new PassImpl(UUID.randomUUID().toString());
        pass.setAccentColor(0xFF0000ff);
        pass.setDescription("custom Pass");
        pass.setApp(APP);
        pass.setType(PassType.EVENT);
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
