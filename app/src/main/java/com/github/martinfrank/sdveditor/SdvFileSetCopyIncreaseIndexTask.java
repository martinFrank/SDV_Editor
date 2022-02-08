package com.github.martinfrank.sdveditor;

import android.widget.Button;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

public class SdvFileSetCopyIncreaseIndexTask extends AbstractSdvFileCopyTask {

    public SdvFileSetCopyIncreaseIndexTask(Button button, SdvFileManager sdvFileManager) {
        super(button, sdvFileManager);
    }

    @Override
    public void executeCopy(SdvFileManager sdvFileManager, SdvFileSet source) {
        sdvFileManager.saveAsNext(source);
    }
}
