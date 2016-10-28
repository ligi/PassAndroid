package org.ligi.passandroid.ui.edit

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.edit_field.view.*
import kotlinx.android.synthetic.main.edit_fields.view.*
import org.ligi.axt.simplifications.SimpleTextWatcher
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.PassField

class FieldsEditFragment : PassandroidFragment() {
    private var isEditingHiddenFields: Boolean = false

    private lateinit var inflater: LayoutInflater

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.inflater = inflater
        val inflate = inflater.inflate(R.layout.edit_fields, container, false)

        isEditingHiddenFields = arguments.getBoolean(ARGUMENT_KEY)

        if (isEditingHiddenFields) {
            inflate.add_field.setText(R.string.add_back_fields)
        } else {
            inflate.add_field.setText(R.string.add_front_field)
        }
        for (passField in pass.fields) {
            if (passField.hide == isEditingHiddenFields) {
                addField(passField, inflate.fields_container)
            }
        }

        inflate.add_field.setOnClickListener {
            val passField = PassField(null, null, null, isEditingHiddenFields)
            addField(passField, inflate.fields_container)
            pass.fields.add(passField)
        }
        return inflate
    }

    fun addField(passField: PassField, viewGroup: ViewGroup) {
        val child = inflater.inflate(R.layout.edit_field, viewGroup, false) as ViewGroup
        FieldView(child).apply(passField, pass.fields)
        viewGroup.addView(child)
    }

    internal inner class FieldView(val container: ViewGroup) {

        fun apply(passField: PassField, fields: MutableList<PassField>) {
            container.label_field_edit.setText(passField.label)
            container.value_field_edit.setText(passField.value)

            container.delete_button.setOnClickListener { v ->
                (v.parent as View).visibility = View.GONE
                fields.remove(passField)
            }

            container.value_field_edit.addTextChangedListener(object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable) {
                    passField.value = s.toString()
                }
            })

            container.label_field_edit.addTextChangedListener(object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable) {
                    passField.label = s.toString()
                }
            })
        }
    }

    companion object {

        private val ARGUMENT_KEY = "KEY"

        fun create(primary: Boolean): FieldsEditFragment {
            val fieldsEditFragment = FieldsEditFragment()
            val args = Bundle()
            args.putBoolean(ARGUMENT_KEY, primary)
            fieldsEditFragment.arguments = args
            return fieldsEditFragment
        }
    }

}
