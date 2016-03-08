package org.ligi.passandroid.helper;

import java.util.UUID;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;

import android.content.Context;
import org.ligi.passandroid.R;

public class PassUtil {

    public static final String ORGANIZATION = "org.ligi.passandroid";
    public static final String APP = ORGANIZATION;

    public static FiledPass createEmptyPass(Context context) {

        final PassImpl pass = new PassImpl();

        pass.setId(UUID.randomUUID().toString());
        pass.setBackgroundColor(context.getResources().getColor(R.color.edit_default_background));
        pass.setDescription(context.getString(R.string.edit_default_title));
        pass.setOrganisation(ORGANIZATION);
        pass.setApp(APP);
        pass.setType(Pass.TYPES[0]);

        return pass;
    }
}
