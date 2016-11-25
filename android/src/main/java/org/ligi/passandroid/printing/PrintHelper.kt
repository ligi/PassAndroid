package org.ligi.passandroid.printing

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.print.PrintManager
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.Pass

@TargetApi(Build.VERSION_CODES.KITKAT)
fun doPrint(context: Context, pass: Pass) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val jobName = context.getString(R.string.app_name) + " print of " + pass.description
    printManager.print(jobName, PassPrintDocumentAdapter(context, pass, jobName), null)
}
