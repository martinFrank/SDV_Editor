package com.github.martinfrank.sdveditor;

import android.widget.Button;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

public class SdvFileSetCopyOverwriteTask extends AbstractSdvFileCopyTask {

    public SdvFileSetCopyOverwriteTask(Button button, SdvFileManager sdvFileManager) {
        super(button, sdvFileManager);
    }

    @Override
    public void executeCopy(SdvFileManager sdvFileManager, SdvFileSet source) {
        sdvFileManager.save(source);
    }
}
