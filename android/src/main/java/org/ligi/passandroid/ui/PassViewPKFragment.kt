package org.ligi.passandroid.ui

import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import android.widget.RelativeLayout
import androidx.core.text.parseAsHtml
import androidx.core.text.util.LinkifyCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_pass_view.*
import kotlinx.android.synthetic.main.barcode.*
import kotlinx.android.synthetic.main.pass_list_item.*
import kotlinx.android.synthetic.main.pkpass_view_extra_data.*
import org.koin.android.ext.android.inject
import org.ligi.kaxt.startActivityFromClass
import org.ligi.passandroid.R
import org.ligi.passandroid.maps.PassbookMapsFacade
import org.ligi.passandroid.model.PassBitmapDefinitions
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.ui.pass_view_holder.VerbosePassViewHolder
import android.util.Log

class PassViewPKFragment : Fragment() {

    private val passViewHelper by lazy { PassViewHelper(requireActivity()) }
    internal val passStore: PassStore by inject()
    lateinit var pass: Pass

    private fun processImage(view: ImageView, name: String, pass: Pass) {
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

    override fun onResume() {
        super.onResume()

        moreTextView.setOnClickListener {
            if (back_fields.visibility == View.VISIBLE) {
                back_fields.visibility = View.GONE
                moreTextView.setText(R.string.more)
            } else {
                back_fields.visibility = View.VISIBLE
                moreTextView.setText(R.string.less)
            }
        }

        val fieldMap : Map<String, ViewGroup> = mapOf(
            "primaryFields" to primary_field_container,
            "auxiliaryFields" to auxiliary_field_container,
            "headerFields" to header_field_container,
            "secondaryFields" to secondary_field_container
        )

        val fieldCount = mutableMapOf<String, Int>()

        fieldMap.forEach {
            fieldCount[it.key] = 0
        }

        barcode_img.setOnClickListener {
            activity?.startActivityFromClass(FullscreenBarcodeActivity::class.java)
        }

        BarcodeUIController(requireView(), pass.barCode, requireActivity(), passViewHelper)

        processImage(logo_img_view, PassBitmapDefinitions.BITMAP_LOGO, pass)
        processImage(footer_img_view, PassBitmapDefinitions.BITMAP_FOOTER, pass)
        processImage(thumbnail_img_view, PassBitmapDefinitions.BITMAP_THUMBNAIL, pass)
        processImage(strip_img_view, PassBitmapDefinitions.BITMAP_STRIP, pass)

        if (map_container != null) {
            if (!(pass.locations.isNotEmpty() && PassbookMapsFacade.init(activity as FragmentActivity))) {
                @Suppress("PLUGIN_WARNING")
                map_container.visibility = View.GONE
            }
        }

        val backStrBuilder = StringBuilder()

        fieldMap.forEach {
            it.value.removeAllViews()
        }

        for (field in pass.fields) {
            val hint = field.hint
            if (field.hide) {
                backStrBuilder.append(field.toHtmlSnippet())
            } else if (hint != null) {
                val v = requireActivity().layoutInflater.inflate(R.layout.vertical_field_item, header_field_container, false)
                val key = v?.findViewById<TextView>(R.id.key)
                key?.text = field.label
                val value = v?.findViewById<TextView>(R.id.value)
                value?.text = field.value
                Log.i("PassAndroid", "creating header with tag = " + field.label + " value = " + field.value)

                if (hint.equals("primaryFields")) {
                    value?.textSize = 40f
                    key?.textSize = 20f
                    val params = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                    if (fieldCount[hint]!! == 0) {
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                        value?.setGravity(Gravity.LEFT)
                        key?.setGravity(Gravity.LEFT)
                        v?.setLayoutParams(params)
                    } else {
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                        value?.setGravity(Gravity.RIGHT)
                        key?.setGravity(Gravity.RIGHT)
                        v?.setLayoutParams(params)
                    }
                }
                fieldMap[hint]!!.addView(v)
                key?.let { LinkifyCompat.addLinks(it, Linkify.ALL) }
                value?.let { LinkifyCompat.addLinks(it, Linkify.ALL) }
                fieldCount[hint] = 1 + fieldCount[hint]!!
                    
            } 
        }

        if (backStrBuilder.isNotEmpty()) {
            back_fields.text = "$backStrBuilder".parseAsHtml()
            moreTextView.visibility = View.VISIBLE
        } else {
            moreTextView.visibility = View.GONE
        }

        LinkifyCompat.addLinks(back_fields, Linkify.ALL)

        val passViewHolder = VerbosePassViewHolder(pass_card)
        passViewHolder.apply(pass, passStore, requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val rootView = inflater.inflate(R.layout.activity_pass_view_page, container, false)
        arguments?.takeIf { it.containsKey(PassViewActivityBase.EXTRA_KEY_UUID) }?.apply {
            val uuid = getString(PassViewActivityBase.EXTRA_KEY_UUID)
            pass = if (uuid != null) {
                passStore.getPassbookForId(uuid) ?: passStore.currentPass!!
            } else {
                passStore.currentPass!!
            }
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val passExtrasView = requireActivity().layoutInflater.inflate(R.layout.pkpass_view_extra_data, passExtrasContainer, false)
        passExtrasContainer.addView(passExtrasView)
    }
}
