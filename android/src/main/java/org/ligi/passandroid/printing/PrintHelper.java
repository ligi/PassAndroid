package org.ligi.passandroid.printing;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.print.PrintManager;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.pass.Pass;

public class PrintHelper {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void doPrint(Context context, Pass pass) {
        final PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        final String jobName = context.getString(R.string.app_name) + " print of " + pass.getDescription();
        printManager.print(jobName, new PassPrintDocumentAdapter(context, pass, jobName), null);
    }

}
