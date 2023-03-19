package org.ligi.passandroid.ui

import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.parseAsHtml
import androidx.core.text.util.LinkifyCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.koin.android.ext.android.inject
import org.ligi.kaxt.startActivityFromClass
import org.ligi.passandroid.R
import org.ligi.passandroid.databinding.ActivityPassViewPageBinding
import org.ligi.passandroid.maps.PassbookMapsFacade
import org.ligi.passandroid.model.PassBitmapDefinitions
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.ui.pass_view_holder.VerbosePassViewHolder

class PassViewFragment : Fragment() {

    private val passViewHelper by lazy { PassViewHelper(requireActivity()) }
    internal val passStore: PassStore by inject()
    lateinit var pass: Pass

    private var _binding: ActivityPassViewPageBinding? = null
    private val binding get() = _binding!!


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

        val moreTextView = requireActivity().findViewById<TextView>(R.id.moreTextView)
        val back_fields = requireActivity().findViewById<TextView>(R.id.back_fields)
        moreTextView.setOnClickListener {

            if (back_fields.visibility == View.VISIBLE) {
                back_fields.visibility = View.GONE
                moreTextView.setText(R.string.more)
            } else {
                back_fields.visibility = View.VISIBLE
                moreTextView.setText(R.string.less)
            }
        }

        requireActivity().findViewById<View>(R.id.barcode_img).setOnClickListener {
            activity?.startActivityFromClass(FullscreenBarcodeActivity::class.java)
        }

        BarcodeUIController(requireView(), pass.barCode, requireActivity(), passViewHelper)

        processImage(requireActivity().findViewById(R.id.logo_img_view), PassBitmapDefinitions.BITMAP_LOGO, pass)
        processImage(requireActivity().findViewById(R.id.footer_img_view), PassBitmapDefinitions.BITMAP_FOOTER, pass)
        processImage(requireActivity().findViewById(R.id.thumbnail_img_view), PassBitmapDefinitions.BITMAP_THUMBNAIL, pass)
        processImage(requireActivity().findViewById(R.id.strip_img_view), PassBitmapDefinitions.BITMAP_STRIP, pass)

        val map_container = requireActivity().findViewById<View>(R.id.map_container)
        if (map_container != null) {
            if (!(pass.locations.isNotEmpty() && PassbookMapsFacade.init(activity as FragmentActivity))) {
                @Suppress("PLUGIN_WARNING")
                map_container.visibility = View.GONE
            }
        }

        val backStrBuilder = StringBuilder()

        val front_field_container = requireActivity().findViewById<LinearLayout>(R.id.front_field_container)
        front_field_container.removeAllViews()

        for (field in pass.fields) {
            if (field.hide) {
                backStrBuilder.append(field.toHtmlSnippet())
            } else {
                val v = requireActivity().layoutInflater.inflate(R.layout.main_field_item, front_field_container, false)
                val key = v?.findViewById<TextView>(R.id.key)
                key?.text = field.label
                val value = v?.findViewById<TextView>(R.id.value)
                value?.text = field.value

                front_field_container.addView(v)
                key?.let { LinkifyCompat.addLinks(it, Linkify.ALL) }
                value?.let { LinkifyCompat.addLinks(it, Linkify.ALL) }
            }
        }

        if (backStrBuilder.isNotEmpty()) {
            back_fields.text = "$backStrBuilder".parseAsHtml()
            moreTextView.visibility = View.VISIBLE
        } else {
            moreTextView.visibility = View.GONE
        }

        LinkifyCompat.addLinks(back_fields, Linkify.ALL)

        val passViewHolder = VerbosePassViewHolder(requireActivity().findViewById(R.id.pass_card))
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

        val passExtrasContainer = view.findViewById<LinearLayout>(R.id.passExtrasContainer)

        val passExtrasView = layoutInflater.inflate(R.layout.pass_view_extra_data, passExtrasContainer, false)
        passExtrasContainer.addView(passExtrasView)
    }
}