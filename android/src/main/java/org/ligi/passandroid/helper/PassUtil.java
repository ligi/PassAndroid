package org.ligi.passandroid.helper;

import java.util.UUID;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;

public class PassUtil {

    public static final String ORGANIZATION = "org.ligi.passandroid";
    public static final String APP = ORGANIZATION;

    public static FiledPass createEmptyPass() {

        final PassImpl pass = new PassImpl();

        pass.setId(UUID.randomUUID().toString());
        pass.setBackgroundColor(0xFF0000ff);
        pass.setDescription("custom Pass");
        pass.setOrganisation(ORGANIZATION);
        pass.setApp(APP);
        pass.setType(Pass.TYPES[0]);

        return pass;
    }
}
