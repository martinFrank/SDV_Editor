package com.github.martinfrank.sdveditor;

import android.util.Log;
import android.widget.Button;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SdvFilesRefreshTask implements Runnable{

//    private final SdvFileManager sdvFileManager;
    private final SdvFileManager sdvFileManager = new SdvFileManager();
    private final File sdvRootDir;
    private final SdvFileSetAdapter sdvFileSetAdapter;
    private final Button button;

    public SdvFilesRefreshTask(File sdvRootDir, SdvFileSetAdapter sdvFileSetAdapter, Button button){
        this.sdvRootDir = sdvRootDir;
        this.sdvFileSetAdapter = sdvFileSetAdapter;
        this.button = button;
    }

    @Override
    public void run() {
        sdvFileSetAdapter.setSelection(-1);
        Log.d(MainActivity.LOG_TAG, "svdRootDir: "+sdvRootDir);
        Log.d(MainActivity.LOG_TAG, "svdRootDir content: "+ Arrays.toString(sdvRootDir.listFiles()));
        List<SdvFileSet> sdvFileSets = sdvFileManager.getSdvFileSets(sdvRootDir);
        Log.d(MainActivity.LOG_TAG, sdvFileSets.toString());
        sdvFileSetAdapter.setData(sdvFileSets);
        button.setEnabled(true);
    }

}
