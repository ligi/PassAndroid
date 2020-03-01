package org.ligi.passandroid.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.ortiz.touch.TouchImageView
import org.koin.android.ext.android.inject
import org.ligi.passandroid.model.PassStore

class TouchImageActivity : AppCompatActivity() {

    val passStore: PassStore by inject()

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
