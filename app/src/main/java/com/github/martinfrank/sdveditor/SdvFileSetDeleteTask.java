package com.github.martinfrank.sdveditor;

import android.util.Log;
import android.widget.Button;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

public class SdvFileSetDeleteTask implements Runnable{

    private final Button button;
    private final SdvFileManager sdvFileManager;
    private SdvFileSet source;

    public SdvFileSetDeleteTask(Button button, SdvFileManager sdvFileManager) {
        this.button = button;
        this.sdvFileManager = sdvFileManager;
    }

    @Override
    public void run() {
        Log.d(MainActivity.LOG_TAG, "run.copy start...");
        sdvFileManager.delete(source);
        Log.d(MainActivity.LOG_TAG, "run.copy done...");
        button.setEnabled(true);
    }

    public void setSource(SdvFileSet source) {
        this.source = source;
    }
}
