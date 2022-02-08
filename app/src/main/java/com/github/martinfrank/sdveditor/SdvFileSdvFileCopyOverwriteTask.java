package com.github.martinfrank.sdveditor;

import android.widget.Button;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

public class SdvFileSdvFileCopyOverwriteTask extends AbstractSdvFileCopyTask {

    public SdvFileSdvFileCopyOverwriteTask(Button button, SdvFileManager sdvFileManager) {
        super(button, sdvFileManager);
    }

    @Override
    public void executCopy(SdvFileManager sdvFileManager, SdvFileSet source) {
        sdvFileManager.save(source);
    }
}
