package com.github.martinfrank.sdveditor;

import android.content.Intent;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

import java.util.List;

public class SdvEditorActivity extends AppCompatActivity {

    public static final String LOCAL_SDVSET_INDEX = "com.github.martinfrank.sdveditor.LOCAL_SDVSET_INDEX";
    public static final String LOG_TAG = "SDV_EDITOR";

    private final SdvFileManager sdvFileManager = new SdvFileManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdv_editor);

        Intent intent = getIntent();
        String message = intent.getStringExtra(LOCAL_SDVSET_INDEX);

        List<SdvFileSet> sdvFileSets = sdvFileManager.getSdvFileSets(getFilesDir());
        SdvFileSet fileSet = sdvFileSets.get(Integer.parseInt(message));
        Log.d(LOG_TAG, "fileset: "+fileSet);




    }
}