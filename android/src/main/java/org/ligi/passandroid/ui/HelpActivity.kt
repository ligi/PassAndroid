package org.ligi.passandroid.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_help.*
import org.ligi.compat.HtmlCompat
import org.ligi.passandroid.BuildConfig
import org.ligi.passandroid.R
import org.xml.sax.XMLReader

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val html = HtmlCompat.fromHtml(getString(R.string.help_content), null, ListTagHandler())

        help_text.text = html
        help_text.movementMethod = LinkMovementMethod()

        setSupportActionBar(toolbar)
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
