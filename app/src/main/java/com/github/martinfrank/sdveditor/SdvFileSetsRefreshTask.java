package com.github.martinfrank.sdveditor;

import android.util.Log;
import android.widget.Button;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

import java.util.List;

public class SdvFileSetsRefreshTask implements Runnable{

    private final SdvFileManager sdvFileManager;
    private final SdvFileSetAdapter sdvFileSetAdapter;
    private final Button button;

    public SdvFileSetsRefreshTask(SdvFileManager sdvFileManager, SdvFileSetAdapter sdvFileSetAdapter, Button button){
        this.sdvFileManager = sdvFileManager;
        this.sdvFileSetAdapter = sdvFileSetAdapter;
        this.button = button;
    }

    @Override
    public void run() {
        button.setEnabled(false);
        sdvFileSetAdapter.setSelection(-1);
        List<SdvFileSet> sdvFileSets = sdvFileManager.loadSdvFileSets();
        Log.d(MainActivity.LOG_TAG, sdvFileSets.toString());
        sdvFileSetAdapter.setData(sdvFileSets);
        button.setEnabled(true);
    }

}
