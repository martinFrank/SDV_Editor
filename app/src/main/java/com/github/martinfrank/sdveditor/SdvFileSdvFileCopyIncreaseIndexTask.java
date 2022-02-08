package com.github.martinfrank.sdveditor;

import android.widget.Button;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

public class SdvFileSdvFileCopyIncreaseIndexTask extends AbstractSdvFileCopyTask {

    public SdvFileSdvFileCopyIncreaseIndexTask(Button button, SdvFileManager sdvFileManager) {
        super(button, sdvFileManager);
    }

    @Override
    public void executCopy(SdvFileManager sdvFileManager, SdvFileSet source) {
        sdvFileManager.saveAsNext(source);
    }
}
