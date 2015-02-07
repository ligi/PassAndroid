package org.ligi.passandroid.helper;

import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;

import java.util.UUID;

public class PassUtil {
    public static FiledPass createEmptyPass() {

        final PassImpl pass = new PassImpl();

        pass.setId(UUID.randomUUID().toString());
        pass.setBackgroundColor(0xFF0000ff);
        pass.setDescription("custom Pass");
        pass.setType(Pass.TYPES[0]);

        return pass;
    }
}
