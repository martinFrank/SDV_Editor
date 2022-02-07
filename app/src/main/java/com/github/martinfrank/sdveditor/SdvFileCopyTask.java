package com.github.martinfrank.sdveditor;

import android.util.Log;
import android.widget.Button;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static android.os.Environment.getExternalStorageDirectory;

public class SdvFileCopyTask implements Runnable {

    private final Button button;
    private final File localDirectory;
    private SdvFileSet source;

    public SdvFileCopyTask(Button button, File localDirectory) {
        this.button = button;
        this.localDirectory = localDirectory;
    }

    @Override
    public void run() {
        button.setEnabled(true);
        File sdvDir = new File(localDirectory, "StardewValley");
        createDirectory(sdvDir);
        copySourceFiles(sdvDir);
    }

    private void copySourceFiles(File sdvDir) {
        File setDir = new File(sdvDir, source.getDirectory().getName());
        createDirectory(setDir);
        for(File f: Objects.requireNonNull(source.getDirectory().listFiles())){
            try {
                Path destFile = new File(setDir, f.getName()).toPath();
                Log.d(MainActivity.LOG_TAG, "copy f:"+f+" to:"+destFile);
                Files.copy(f.toPath(), destFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void setSource(SdvFileSet source) {
        this.source = source;
    }

    private void createDirectory(File dir){
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
