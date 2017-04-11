package org.ligi.passandroid.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.salomonbrys.kodein.instance
import org.ligi.passandroid.App
import org.ligi.passandroid.Tracker
import org.ligi.passandroid.model.PassStore

class PassImportActivity : AppCompatActivity() {

    val tracker: Tracker = App.kodein.instance()
    val passStore: PassStore = App.kodein.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.data == null || intent.data.scheme == null) {
            tracker.trackException("invalid_import_uri", false)
            finish()
        } else {
            ImportAndShowAsyncTask(this, intent.data).execute()
        }
    }

}
