package com.github.martinfrank.sdveditor;

import android.util.Log;
import android.widget.Button;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

public abstract class AbstractSdvFileCopyTask implements Runnable{

    private final Button button;
    private final SdvFileManager sdvFileManager;
    private SdvFileSet source;

    public AbstractSdvFileCopyTask(Button button, SdvFileManager sdvFileManager) {
        this.button = button;
        this.sdvFileManager = sdvFileManager;
    }

    @Override
    public void run() {
        Log.d(MainActivity.LOG_TAG, "run.copy start...");
        executCopy(sdvFileManager, source);
        Log.d(MainActivity.LOG_TAG, "run.copy done...");
        button.setEnabled(true);
    }

    public abstract void executCopy(SdvFileManager sdvFileManager, SdvFileSet source);

    public void setSource(SdvFileSet source) {
        this.source = source;
    }
}
