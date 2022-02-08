package com.github.martinfrank.sdveditor;

import android.content.Intent;
import android.text.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SdvEditorActivity extends AppCompatActivity {

    public static final String LOCAL_SDVSET_INDEX = "com.github.martinfrank.sdveditor.LOCAL_SDVSET_INDEX";
    public static final String LOG_TAG = "SDV_EDITOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdv_editor);

        Intent intent = getIntent();
        String message = intent.getStringExtra(LOCAL_SDVSET_INDEX);
        SdvFileManager sdvFileManager = new SdvFileManager(getFilesDir());

        List<SdvFileSet> sdvFileSets = sdvFileManager.loadSdvFileSets();
        SdvFileSet fileSet = sdvFileSets.get(Integer.parseInt(message));
        Log.d(LOG_TAG, "fileset: " + fileSet);

        LinearLayout linearLayout = findViewById(R.id.property_container);
        LayoutInflater inflater = LayoutInflater.from(this);

        View row = inflater.inflate(R.layout.editor_row, linearLayout);

        Consumer<String> setter = (value) -> fileSet.setMoney(Integer.parseInt(value));
        Supplier<String> getter = () -> ""+fileSet.getMoney();
        createRow(row, "Money", getter, setter, InputType.TYPE_CLASS_NUMBER, new MinMaxFilter(0,999999999));


    }

    private void createRow(View row, String propertyName, Supplier<String> getter, Consumer<String> setter, int fieldType, InputFilter... filter) {
        TextView property_name = row.findViewById(R.id.property_name);
        property_name.setText(propertyName);
        TextView property_original = row.findViewById(R.id.property_original);
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
}