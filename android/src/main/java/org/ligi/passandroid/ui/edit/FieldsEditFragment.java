package org.ligi.passandroid.ui.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.List;
import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.pass.PassField;

public class FieldsEditFragment extends PassandroidFragment {

    private final static String ARGUMENT_KEY = "KEY";
    private boolean isEditingHiddenFields;

    public static FieldsEditFragment create(boolean primary) {
        final FieldsEditFragment fieldsEditFragment = new FieldsEditFragment();
        final Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_KEY, primary);
        fieldsEditFragment.setArguments(args);
        return fieldsEditFragment;
    }

    @Bind(R.id.fields_container)
    ViewGroup viewGroup;

    @Bind(R.id.add_field)
    Button addFieldButton;

    @OnClick(R.id.add_field)
    void onAddField() {
        final PassField passField = new PassField(null, null, null, isEditingHiddenFields);
        addField(passField);
        getPass().getFields().add(passField);
    }

    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        final View inflate = inflater.inflate(R.layout.edit_fields, container, false);

        ButterKnife.bind(this, inflate);

        isEditingHiddenFields = getArguments().getBoolean(ARGUMENT_KEY);

        if (isEditingHiddenFields) {
            addFieldButton.setText(R.string.add_back_fields);
        } else {
            addFieldButton.setText(R.string.add_front_field);
        }
        for (final PassField passField : getPass().getFields()) {
            if (passField.getHide() == isEditingHiddenFields) {
                addField(passField);
            }
        }

        return inflate;
    }

    public void addField(final PassField passField) {
        final ViewGroup child = (ViewGroup) inflater.inflate(R.layout.edit_field, viewGroup, false);
        new FieldView(child).apply(passField, getPass().getFields());
        viewGroup.addView(child);
    }

    class FieldView {
        @Bind(R.id.label_field_edit)
        TextInputEditText labelEdit;

        @Bind(R.id.value_field_edit)
        TextInputEditText valueEdit;

        @Bind(R.id.delete_button)
        ImageButton deleteButton;

        FieldView(final ViewGroup container) {
            ButterKnife.bind(this, container);
        }

        public void apply(final PassField passField, final List<PassField> fields) {
            labelEdit.setText(passField.getLabel());
            valueEdit.setText(passField.getValue());

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    ((View) (v.getParent())).setVisibility(View.GONE);
                    fields.remove(passField);
                }
            });

            valueEdit.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(final Editable s) {
                    passField.setValue(s.toString());
                }
            });

            labelEdit.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(final Editable s) {
                    passField.setLabel(s.toString());
                }
            });
        }
    }

}
