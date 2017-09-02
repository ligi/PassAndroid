package org.ligi.passandroid.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.text.util.LinkifyCompat
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_pass_view.*
import kotlinx.android.synthetic.main.activity_pass_view_base.*
import kotlinx.android.synthetic.main.barcode.*
import kotlinx.android.synthetic.main.edit.*
import kotlinx.android.synthetic.main.pass_view_extra_data.*
import org.ligi.compat.HtmlCompat
import org.ligi.kaxt.disableRotation
import org.ligi.kaxt.startActivityFromClass
import org.ligi.passandroid.R
import org.ligi.passandroid.maps.PassbookMapsFacade
import org.ligi.passandroid.model.PassBitmapDefinitions
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.ui.pass_view_holder.VerbosePassViewHolder

class PassViewActivity : PassViewActivityBase() {

    val passViewHelper by lazy { PassViewHelper(this) }

    internal fun processImage(view: ImageView, name: String, pass: Pass) {
        val bitmap = pass.getBitmap(passStore, name)
        if (bitmap != null && bitmap.width > 300) {
            view.setOnClickListener {
                val intent = Intent(view.context, TouchImageActivity::class.java)
                intent.putExtra("IMAGE", name)
                startActivity(intent)
            }
        }
        passViewHelper.setBitmapSafe(view, bitmap)
    }

    override fun refresh() {
        super.refresh()

        BarcodeUIController(findViewById(android.R.id.content), currentPass.barCode, this, passViewHelper)

        processImage(logo_img_view, PassBitmapDefinitions.BITMAP_LOGO, currentPass)
        processImage(footer_img_view, PassBitmapDefinitions.BITMAP_FOOTER, currentPass)
        processImage(thumbnail_img_view, PassBitmapDefinitions.BITMAP_THUMBNAIL, currentPass)
        processImage(strip_img_view, PassBitmapDefinitions.BITMAP_STRIP, currentPass)

        if (map_container != null) {
            if (!(currentPass.locations.isNotEmpty() && PassbookMapsFacade.init(this))) {
                map_container.visibility = View.GONE
            }
        }

        val back_str = StringBuilder()

        front_field_container.removeAllViews()

        for (field in currentPass.fields) {
            if (field.hide) {
                back_str.append(field.toHtmlSnippet())
            } else {
                val v = layoutInflater.inflate(R.layout.main_field_item, front_field_container, false)
                val key = v.findViewById(R.id.key) as TextView
                key.text = field.label
                val value = v.findViewById(R.id.value) as TextView
                value.text = field.value

                front_field_container.addView(v)
                LinkifyCompat.addLinks(key, Linkify.ALL)
                LinkifyCompat.addLinks(value, Linkify.ALL)
            }
        }


        if (back_str.isNotEmpty()) {
            back_fields.text = HtmlCompat.fromHtml(back_str.toString())
            moreTextView.visibility = View.VISIBLE
        } else {
            moreTextView.visibility = View.GONE
        }


        LinkifyCompat.addLinks(back_fields, Linkify.ALL)

        val passViewHolder = VerbosePassViewHolder(pass_card)
        passViewHolder.apply(currentPass, passStore, this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        disableRotation()

        setContentView(R.layout.activity_pass_view)

        val passExtrasView = layoutInflater.inflate(R.layout.pass_view_extra_data, passExtrasContainer, false)
        passExtrasContainer.addView(passExtrasView)

    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        moreTextView.setOnClickListener {
            if (back_fields.visibility == View.VISIBLE) {
                back_fields.visibility = View.GONE
                moreTextView.setText(R.string.more)
            } else {
                back_fields.visibility = View.VISIBLE
                moreTextView.setText(R.string.less)
            }
        }

        barcode_img.setOnClickListener {
            startActivityFromClass(FullscreenBarcodeActivity::class.java)
        }

        setSupportActionBar(toolbar)

        configureActionBar()
        refresh()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_map).isVisible = !currentPass.locations.isEmpty()
        menu.findItem(R.id.menu_update).isVisible = PassViewActivityBase.mightPassBeAbleToUpdate(currentPass)
        menu.findItem(R.id.install_shortcut).isVisible = (23..25).contains(Build.VERSION.SDK_INT)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.map_item, menu)
        menuInflater.inflate(R.menu.update, menu)
        menuInflater.inflate(R.menu.shortcut, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            val upIntent = NavUtils.getParentActivityIntent(this)
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities()
                finish()
            } else {
                NavUtils.navigateUpTo(this, upIntent)
            }
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onAttachedToWindow() = window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)

}
