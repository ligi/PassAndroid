package org.ligi.passandroid.ui.edit

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.RadioButton
import kotlinx.android.synthetic.main.barcode_edit.view.*
import org.ligi.kaxt.doAfterEdit
import org.ligi.passandroid.model.pass.BarCode
import org.ligi.passandroid.model.pass.PassBarCodeFormat
import org.ligi.passandroid.model.pass.PassBarCodeFormat.*
import org.ligi.passandroid.ui.BarcodeUIController
import org.ligi.passandroid.ui.PassViewHelper
import java.util.*

class BarcodeEditController(private val rootView: View, internal val context: AppCompatActivity, barCode: BarCode) {
    private var barcodeFormat: PassBarCodeFormat?
    private val intentFragment: Fragment
    private val passFormatRadioButtons: MutableMap<PassBarCodeFormat, RadioButton> = EnumMap(PassBarCodeFormat::class.java)

    class IntentFragment : Fragment() {
        var scanCallback: (Pair<String, String>) -> Unit = {}

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (data != null && data.hasExtra("SCAN_RESULT_FORMAT") && data.hasExtra("SCAN_RESULT")) {
                scanCallback(Pair(data.getStringExtra("SCAN_RESULT_FORMAT"), data.getStringExtra("SCAN_RESULT")))
            }
        }
    }

    private fun bindRadio(formats: Array<PassBarCodeFormat>) {
        formats.forEach {
            val radioButton = RadioButton(context)
            rootView.barcodeRadioGroup.addView(radioButton)
            passFormatRadioButtons[it] = radioButton

            radioButton.text = it.name
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    barcodeFormat = it
                    refresh()
                }
            }

            radioButton.isChecked = barcodeFormat == it
        }

    }

    init {
        intentFragment = IntentFragment()
        barcodeFormat = barCode.format

        rootView.randomButton.setOnClickListener {
            rootView.messageInput.setText(when (barcodeFormat) {
                EAN_8 -> getRandomEAN8()
                EAN_13 -> getRandomEAN13()
                ITF -> getRandomITF()
                else -> UUID.randomUUID().toString().toUpperCase()
            })
            refresh()
        }

        rootView.scanButton.setOnClickListener {
            val barCodeIntentIntegrator = BarCodeIntentIntegrator(intentFragment)
            barCodeIntentIntegrator.initiateScan(PassBarCodeFormat.values().map { it.name })
        }

        intentFragment.scanCallback = { (newFormat, newMessage) ->
            rootView.messageInput.setText(newMessage)
            rootView.barcodeRadioGroup.check(passFormatRadioButtons[PassBarCodeFormat.valueOf(newFormat)]!!.id)
            refresh()
        }
        context.supportFragmentManager.beginTransaction().add(intentFragment, "intent_fragment").commit()

        bindRadio(PassBarCodeFormat.values())

        rootView.messageInput.setText(barCode.message)
        rootView.messageInput.doAfterEdit {
            refresh()
        }

        rootView.alternativeMessageInput.setText(barCode.alternativeText)

        refresh()
    }

    fun refresh() {
        val barcodeUIController = BarcodeUIController(rootView, getBarCode(), context, PassViewHelper(context))
        val isBarcodeShown = barcodeUIController.getBarcodeView().visibility == View.VISIBLE

        if (!isBarcodeShown) {
            rootView.messageInput.error = "Invalid message"
        } else {
            rootView.messageInput.error = null
        }
    }

    fun getBarCode() = BarCode(barcodeFormat, rootView.messageInput.text.toString()).apply {
        val newAlternativeText = rootView.alternativeMessageInput.text.toString()
        if (!newAlternativeText.isEmpty()) {
            alternativeText = newAlternativeText
        }
    }
}
