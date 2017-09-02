package org.ligi.passandroid.ui.edit

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.edit_field.view.*
import kotlinx.android.synthetic.main.edit_fields.view.*
import org.ligi.kaxt.doAfterEdit
import org.ligi.passandroid.App
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.PassField
import org.ligi.passandroid.model.pass.PassImpl


class FieldsEditFragment : Fragment() {

    private fun getPass(): PassImpl = App.passStore.currentPass as PassImpl

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
        getPass().fields
                .filter { it.hide == isEditingHiddenFields }
                .forEach { addField(it, inflate.fields_container) }

        inflate.add_field.setOnClickListener {
            val passField = PassField(null, null, null, isEditingHiddenFields)
            addField(passField, inflate.fields_container)
            getPass().fields.add(passField)
        }
        return inflate
    }

    private fun addField(passField: PassField, viewGroup: ViewGroup) {
        val child = inflater.inflate(R.layout.edit_field, viewGroup, false) as ViewGroup
        FieldView(child).apply(passField, getPass().fields)
        viewGroup.addView(child)
    }

    internal inner class FieldView(private val container: ViewGroup) {

        fun apply(passField: PassField, fields: MutableList<PassField>) {
            container.label_field_edit.setText(passField.label)
            container.value_field_edit.setText(passField.value)

            container.delete_button.setOnClickListener { v ->
                (v.parent as View).visibility = View.GONE
                fields.remove(passField)
            }

            container.value_field_edit.doAfterEdit {
                passField.value = it.toString()
            }

            container.label_field_edit.doAfterEdit {
                passField.label = it.toString()
            }
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
