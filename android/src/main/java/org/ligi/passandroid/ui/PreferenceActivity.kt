package org.ligi.passandroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import org.ligi.passandroid.R

class PreferenceActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.preferences)

        val prefs: View? = findViewById(android.R.id.content)
        if (prefs != null) {
            ViewCompat.setOnApplyWindowInsetsListener(prefs){ v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                // Apply the insets as a margin to the view. This solution sets
                // only the bottom, left, and right dimensions, but you can apply whichever
                // insets are appropriate to your layout. You can also update the view padding
                // if that's more appropriate.
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = insets.top
                    leftMargin = insets.left
                    bottomMargin = insets.bottom
                    rightMargin = insets.right
                }

                // Return CONSUMED if you don't want want the window insets to keep passing
                // down to descendant views.
                WindowInsetsCompat.CONSUMED
            }
        }
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @SuppressLint("PrivateResource")
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}


