package com.github.martinfrank.sdveditor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.martinfrank.sdvedit.SdvFileManager;
import com.github.martinfrank.sdvedit.SdvFileSet;

import java.util.Collections;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MAIN_ACTIVITY";

    private Console console;
    private Button remoteRefreshButton;
    private Button localRefreshButton;
    private Button fetchButton;
    private SdvFilesRefreshTask remoteSdvFilesRefreshTask;
    private SdvFilesRefreshTask localSdvFilesRefreshTask;
    private SdvFileSdvFileCopyOverwriteTask copyRemoteToLocalOverrideTask;
    private SdvFileSdvFileCopyIncreaseIndexTask copyRemoteToLocalIncreaseIndexTask;
    private Handler handler;
    private SdvFileSetAdapter remoteSdvFileSetAdapter;
    private SdvFileSetAdapter localSdvFileSetAdapter;
    private TextView remoteSdvInfoTextView;
    private TextView localSdvInfoTextView;
    private SdvFileManager remoteSdvFileManager;
    private SdvFileManager localSdvFileManager;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 4711);

        console = new Console(findViewById(R.id.console));

        Button editButton = findViewById(R.id.edit_local);
        editButton.setOnClickListener(view -> startEditor());

        RecyclerView remoteSdvFilesRecyclerView = findViewById(R.id.remote_sdv_files);
        RecyclerView localSdvFilesRecyclerView = findViewById(R.id.local_sdv_files);

        final View.OnClickListener remoteSdvFilesOnclickListener = this::updateRemoteSdvFileInfoFromView;

        remoteSdvInfoTextView = findViewById(R.id.sdv_file_info);
        localSdvInfoTextView = findViewById(R.id.local_file_info);

        remoteSdvFileManager = new SdvFileManager(getExternalStorageDirectory());
        localSdvFileManager = new SdvFileManager(getFilesDir());

        remoteSdvFileSetAdapter = new SdvFileSetAdapter(Collections.emptyList(), remoteSdvFilesOnclickListener);
        remoteSdvFilesRecyclerView.setAdapter(remoteSdvFileSetAdapter);

        final View.OnClickListener localSdvFilesOnclickListener = this::updateLocalSdvFileInfoFromView;

        localSdvFileSetAdapter = new SdvFileSetAdapter(Collections.emptyList(), localSdvFilesOnclickListener);
        localSdvFilesRecyclerView.setAdapter(localSdvFileSetAdapter);


        LinearLayoutManager remoteLayoutManager = new LinearLayoutManager(this);
        remoteSdvFilesRecyclerView.setLayoutManager(remoteLayoutManager);

        LinearLayoutManager localLayoutManager = new LinearLayoutManager(this);
        localSdvFilesRecyclerView.setLayoutManager(localLayoutManager);

        remoteRefreshButton = findViewById(R.id.refresh_remote_sdv_files);
        remoteRefreshButton.setOnClickListener(view -> refreshRemoteSdvFiles());

        localRefreshButton = findViewById(R.id.refresh_local_sdv_files);
        localRefreshButton.setOnClickListener(view -> refreshLocalSdvFiles());

        fetchButton = findViewById(R.id.copy_from_remote_to_local);
        fetchButton.setOnClickListener(view -> copyFromRemoteToLocal());

        remoteSdvFilesRefreshTask = new SdvFilesRefreshTask(remoteSdvFileManager, remoteSdvFileSetAdapter, remoteRefreshButton);
        localSdvFilesRefreshTask = new SdvFilesRefreshTask(localSdvFileManager, localSdvFileSetAdapter, localRefreshButton);

        copyRemoteToLocalOverrideTask = new SdvFileSdvFileCopyOverwriteTask(fetchButton, localSdvFileManager);
        copyRemoteToLocalIncreaseIndexTask = new SdvFileSdvFileCopyIncreaseIndexTask(fetchButton, localSdvFileManager);

        handler = new Handler(Looper.getMainLooper());
    }

    private void startEditor() {

        Intent intent = new Intent(this, SdvEditorActivity.class);
        String selection = "" + localSdvFileSetAdapter.getSelection();
        intent.putExtra(SdvEditorActivity.LOCAL_SDVSET_INDEX, selection);

        localSdvFileSetAdapter.setSelection(-1);
        localSdvInfoTextView.setText("");

        startActivity(intent);

    }

    private void refreshLocalSdvFiles() {
        console.print("refreshing local sdv file list - start...");
        localRefreshButton.setEnabled(false);
        localSdvInfoTextView.setText("");
        handler.post(() -> localSdvFilesRefreshTask.run());
    }

    private void copyFromRemoteToLocal() {
        int index = remoteSdvFileSetAdapter.getSelection();
        if (index >= 0) {
            SdvFileSet sdvFileSet = remoteSdvFileSetAdapter.getFileSet(index);
            if (localSdvFileManager.exists(sdvFileSet)) {
                askForCopyRemoteToLocalAction(sdvFileSet);
            }else{
                copyRemoteToLocalOverride(sdvFileSet);
            }
        }
    }



    private void askForCopyRemoteToLocalAction(SdvFileSet sdvFileSet) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Please Select any option");
        dialog.setTitle("Dialog Box");
        dialog.setPositiveButton("OVERWRITE", (diag, which) -> copyRemoteToLocalOverride(sdvFileSet));
        dialog.setNegativeButton("COPY", (diag, which) -> copyRemoteToLocalIncreaseIndex(sdvFileSet));
        dialog.setCancelable(true);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void askForCopyLocalToRemoteAction(SdvFileSet sdvFileSet) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Please Select any option");
        dialog.setTitle("Dialog Box");
        dialog.setPositiveButton("OVERWRITE", (diag, which) -> copyRemoteToLocalOverride(sdvFileSet));
        dialog.setNegativeButton("COPY", (diag, which) -> copyRemoteToLocalIncreaseIndex(sdvFileSet));
        dialog.setCancelable(true);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void copyRemoteToLocalIncreaseIndex(SdvFileSet sdvFileSet) {
        fetchButton.setEnabled(false);
        copyRemoteToLocalIncreaseIndexTask.setSource(sdvFileSet);
        handler.post(() -> copyRemoteToLocalIncreaseIndexTask.run());
        handler.post(() -> localSdvFilesRefreshTask.run());
    }

    private void copyRemoteToLocalOverride(SdvFileSet sdvFileSet) {
        fetchButton.setEnabled(false);
        copyRemoteToLocalOverrideTask.setSource(sdvFileSet);
        handler.post(() -> copyRemoteToLocalOverrideTask.run());
        handler.post(() -> localSdvFilesRefreshTask.run());
    }

    private void updateRemoteSdvFileInfoFromView(View view) {
        TextView indexHolder = view.findViewById(R.id.index_holder);
        int index = Integer.parseInt("" + indexHolder.getText());
        SdvFileSet fileSet = remoteSdvFileSetAdapter.getFileSet(index);
        remoteSdvFileSetAdapter.setSelection(index);
        updateRemoteSdvFileInfo(fileSet);
    }

    private void updateLocalSdvFileInfoFromView(View view) {
        TextView indexHolder = view.findViewById(R.id.index_holder);
        int index = Integer.parseInt("" + indexHolder.getText());
        SdvFileSet fileSet = localSdvFileSetAdapter.getFileSet(index);
        localSdvFileSetAdapter.setSelection(index);
        updateLocalSdvFileInfo(fileSet);
    }

    private void updateRemoteSdvFileInfo(SdvFileSet t) {
        updateSdvFileInfo(t, remoteSdvInfoTextView);
    }

    private void updateLocalSdvFileInfo(SdvFileSet t) {
        updateSdvFileInfo(t, localSdvInfoTextView);
    }

    private void updateSdvFileInfo(SdvFileSet t, TextView textView) {
        String info = "fileset: " + t.toString() + "\n" +
                "player: " + t.getName() + "\n" +
                "money: " + t.getMoney();
        textView.setText(info);
        console.print(t.toString());
    }

    private void refreshRemoteSdvFiles() {
        console.print("refreshing remote sdv file list - start...");
        remoteRefreshButton.setEnabled(false);
        remoteSdvInfoTextView.setText("");
        handler.post(() -> remoteSdvFilesRefreshTask.run());
    }

}