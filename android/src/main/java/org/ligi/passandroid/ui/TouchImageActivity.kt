package org.ligi.passandroid.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.ortiz.touch.TouchImageView
import org.ligi.passandroid.App
import org.ligi.passandroid.model.PassStore
import javax.inject.Inject

class TouchImageActivity : AppCompatActivity() {

    @Inject
    lateinit var passStore: PassStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        val webView = TouchImageView(this)

        setContentView(webView)

        webView.setImageBitmap(passStore.currentPass!!.getBitmap(passStore, intent.getStringExtra("IMAGE")))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
