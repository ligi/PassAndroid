package org.ligi.passandroid.helper;

import java.util.UUID;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.model.PassType;

public class PassUtil {

    public static final String APP = "passandroid";

    public static Pass createEmptyPass() {
        final PassImpl pass = new PassImpl(UUID.randomUUID().toString());
        pass.setAccentColor(0xFF0000ff);
        pass.setDescription("custom Pass");
        pass.setApp(APP);
        pass.setType(PassType.EVENT);
        return pass;
    }
}
