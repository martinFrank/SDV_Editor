package com.github.martinfrank.sdveditor;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SdvEditorActivity extends AppCompatActivity {

    public static final String LOCAL_SDVFILESET_INDEX = "com.github.martinfrank.sdveditor.LOCAL_SDVFILESET_INDEX";
    public static final String LOG_TAG = "SDV_EDITOR";

    private final List<PropertyRow> properties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdv_editor);

        Intent intent = getIntent();
        String message = intent.getStringExtra(LOCAL_SDVFILESET_INDEX);
        SdvFileManager sdvFileManager = new SdvFileManager(getFilesDir());

        List<SdvFileSet> sdvFileSets = sdvFileManager.loadSdvFileSets();
        SdvFileSet fileSet = sdvFileSets.get(Integer.parseInt(message));
        Log.d(LOG_TAG, "fileset: " + fileSet);

        Button cancelButton = findViewById(R.id.cancel_edit);
        cancelButton.setOnClickListener(view -> finish());

        Button saveButton = findViewById(R.id.saveChanges);
        saveButton.setOnClickListener(view -> askForSave(fileSet));

        LinearLayout linearLayout = findViewById(R.id.property_container);
        LayoutInflater inflater = LayoutInflater.from(this);

        View row = inflater.inflate(R.layout.editor_row, linearLayout);

        Consumer<String> setter = (value) -> fileSet.setMoney(Integer.parseInt(value));
        Supplier<String> getter = () -> ""+fileSet.getMoney();
        PropertyRow propertyRow = new PropertyRow(row, "Money", getter, setter, InputType.TYPE_CLASS_NUMBER, new MinMaxFilter(0,99999999));
        properties.add(propertyRow);
    }


    @Override
    public void finish() {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
        alterDialog.setMessage(R.string.cancel_editing_message);
        alterDialog.setTitle(R.string.cancel_editing_title);
        alterDialog.setPositiveButton(R.string.cancel_editing_confirm, (dialog, which) -> super.finish());
        alterDialog.setCancelable(true);
        AlertDialog alertDialog = alterDialog.create();
        alertDialog.show();
    }

    private void askForSave(SdvFileSet fileSet) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
        alterDialog.setMessage(R.string.confirm_save_changes_message);
        alterDialog.setTitle(R.string.confirm_save_changes_title);
        alterDialog.setPositiveButton(R.string.confirm_save_changes_confirm, (dialog, which) -> saveChanges(fileSet));
        alterDialog.setCancelable(true);
        AlertDialog alertDialog = alterDialog.create();
        alertDialog.show();
    }

    private void saveChanges(SdvFileSet fileSet) {
        //FIXME as Async Task!!
        fileSet.saveChanges();
        properties.forEach(PropertyRow::updateOriginal);
    }

}