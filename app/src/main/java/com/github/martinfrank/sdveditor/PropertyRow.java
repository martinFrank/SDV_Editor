package com.github.martinfrank.sdveditor;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PropertyRow {

    private final Supplier<String> getter;
    private final TextView property_original;

    public PropertyRow(View row, String propertyName, Supplier<String> getter, Consumer<String> setter, int fieldType, InputFilter... filter) {
        this.getter = getter;
        TextView property_name = row.findViewById(R.id.property_name);
        property_name.setText(propertyName);
        property_original = row.findViewById(R.id.property_original);
        property_original.setText(getter.get());
        EditText property_editor = row.findViewById(R.id.property_edit);
        property_editor.setText(getter.get());
        property_editor.setInputType(fieldType);
        property_editor.setFilters(filter);
        property_editor.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                setter.accept(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

        });
    }

    public void updateOriginal() {
        property_original.setText(getter.get());
    }
}
