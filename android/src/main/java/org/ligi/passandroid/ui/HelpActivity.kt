package org.ligi.passandroid.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import org.ligi.compat.HtmlCompat
import org.ligi.passandroid.BuildConfig
import org.ligi.passandroid.R
import org.ligi.passandroid.databinding.ActivityHelpBinding
import org.xml.sax.XMLReader

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val html = HtmlCompat.fromHtml(getString(R.string.help_content), null, ListTagHandler())

        binding.helpText.text = html
        binding.helpText.movementMethod = LinkMovementMethod()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.subtitle = "v" + BuildConfig.VERSION_NAME
    }

    internal inner class ListTagHandler : Html.TagHandler {

        override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
            if (tag.equals("li", ignoreCase = true)) {
                output.append(if (opening) "\u2022 " else "\n")
            }
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
