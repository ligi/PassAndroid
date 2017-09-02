package org.ligi.passandroid.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.salomonbrys.kodein.instance
import com.ortiz.touch.TouchImageView
import org.ligi.passandroid.App
import org.ligi.passandroid.model.PassStore

class TouchImageActivity : AppCompatActivity() {

    val passStore: PassStore = App.kodein.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val touchImageView = TouchImageView(this)

        setContentView(touchImageView)

        val bitmap = passStore.currentPass?.getBitmap(passStore, intent.getStringExtra("IMAGE"))

        if (bitmap == null) {
            finish()
        } else {
            touchImageView.setImageBitmap(bitmap)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
