package org.ligi.passandroid.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.ligi.passandroid.App
import org.ligi.passandroid.Tracker
import org.ligi.passandroid.model.PassStore
import javax.inject.Inject

class PassImportActivity : AppCompatActivity() {

    @Inject
    lateinit var tracker: Tracker

    @Inject
    lateinit var passStore: PassStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component.inject(this)

        if (intent.data == null || intent.data.scheme == null) {
            tracker.trackException("invalid_import_uri", false)
            finish()
        } else {
            ImportAndShowAsyncTask(this, intent.data).execute()
        }
    }

}
