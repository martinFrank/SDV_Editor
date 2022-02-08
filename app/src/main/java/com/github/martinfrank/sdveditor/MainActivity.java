package com.github.martinfrank.sdveditor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private Button pushButton;
    private Button deleteButton;
    private SdvFileSetsRefreshTask remoteSdvFileSetsRefreshTask;
    private SdvFileSetsRefreshTask localSdvFileSetsRefreshTask;
    private SdvFileSetCopyOverwriteTask copyRemoteToLocalOverrideTask;
    private SdvFileSetCopyIncreaseIndexTask copyRemoteToLocalIncreaseIndexTask;
    private SdvFileSetCopyOverwriteTask copyLocalToRemoteOverrideTask;
    private SdvFileSetCopyIncreaseIndexTask copyLocalToRemoteIncreaseIndexTask;
    private SdvFileSetDeleteTask deleteLocalTask;
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

        deleteButton = findViewById(R.id.delete_local);
        deleteButton.setOnClickListener(view -> askForDeleteLocal());

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

        pushButton = findViewById(R.id.copy_to_remote_from_local);
        pushButton.setOnClickListener(view -> copyToRemoteFromLocal());

        remoteSdvFileSetsRefreshTask = new SdvFileSetsRefreshTask(remoteSdvFileManager, remoteSdvFileSetAdapter, remoteRefreshButton);
        localSdvFileSetsRefreshTask = new SdvFileSetsRefreshTask(localSdvFileManager, localSdvFileSetAdapter, localRefreshButton);

        copyRemoteToLocalOverrideTask = new SdvFileSetCopyOverwriteTask(fetchButton, localSdvFileManager);
        copyRemoteToLocalIncreaseIndexTask = new SdvFileSetCopyIncreaseIndexTask(fetchButton, localSdvFileManager);

        copyLocalToRemoteOverrideTask = new SdvFileSetCopyOverwriteTask(pushButton, remoteSdvFileManager);
        copyLocalToRemoteIncreaseIndexTask = new SdvFileSetCopyIncreaseIndexTask(pushButton, remoteSdvFileManager);

        deleteLocalTask =new SdvFileSetDeleteTask(deleteButton, localSdvFileManager);

        handler = new Handler(Looper.getMainLooper());
    }

    private void askForDeleteLocal() {
        int index = localSdvFileSetAdapter.getSelection();
        if (index >= 0) {
            SdvFileSet sdvFileSet = localSdvFileSetAdapter.getFileSet(index);
            AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
            alterDialog.setMessage("really delete Savegame?");
            alterDialog.setTitle("Confirm deletion");
            alterDialog.setPositiveButton("CONFIRM", (dialog, which) -> deleteLocal(sdvFileSet));
            alterDialog.setCancelable(true);
            AlertDialog alertDialog = alterDialog.create();
            alertDialog.show();
        }
    }

    private void deleteLocal(SdvFileSet sdvFileSet) {
        //FIXME Task needed?
        deleteButton.setEnabled(false);
        deleteLocalTask.setSource(sdvFileSet);
        handler.post(() -> deleteLocalTask.run());
        refreshLocalSdvFiles();
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
        handler.post(() -> localSdvFileSetsRefreshTask.run());
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

    private void copyToRemoteFromLocal() {
        int index = localSdvFileSetAdapter.getSelection();
        if (index >= 0) {
            SdvFileSet sdvFileSet = localSdvFileSetAdapter.getFileSet(index);
            if (remoteSdvFileManager.exists(sdvFileSet)) {
                askForCopyLocalToRemoteAction(sdvFileSet);
            }else{
                copyRemoteToLocalOverride(sdvFileSet);
            }
        }
    }



    private void askForCopyRemoteToLocalAction(SdvFileSet sdvFileSet) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
        alterDialog.setMessage("This Savegame already exists local! What would you like to do?");
        alterDialog.setTitle("Savegame already exists");
        alterDialog.setPositiveButton("OVERWRITE", (dialog, which) -> copyRemoteToLocalOverride(sdvFileSet));
        alterDialog.setNegativeButton("COPY", (dialog, which) -> copyRemoteToLocalIncreaseIndex(sdvFileSet));
        alterDialog.setCancelable(true);
        AlertDialog alertDialog = alterDialog.create();
        alertDialog.show();
    }

    private void askForCopyLocalToRemoteAction(SdvFileSet sdvFileSet) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
        alterDialog.setMessage("This Savegame already exists in your Game! What would you like to do?");
        alterDialog.setTitle("Savegame already exists");
        alterDialog.setPositiveButton(R.string.app_name, (dialog, which) -> copyLocalToRemoteOverride(sdvFileSet));//FIXME: "OVERWRITE"
        alterDialog.setNegativeButton("COPY", (dialog, which) -> copyLocalToRemoteIncreaseIndex(sdvFileSet));
        alterDialog.setCancelable(true);
        AlertDialog alertDialog = alterDialog.create();
        alertDialog.show();
    }

    private void copyLocalToRemoteIncreaseIndex(SdvFileSet sdvFileSet) {
        pushButton.setEnabled(false);
        copyLocalToRemoteIncreaseIndexTask.setSource(sdvFileSet);
        handler.post(() -> copyLocalToRemoteIncreaseIndexTask.run());
        refreshRemoteSdvFiles();
    }

    private void copyLocalToRemoteOverride(SdvFileSet sdvFileSet) {
        pushButton.setEnabled(false);
        copyLocalToRemoteOverrideTask.setSource(sdvFileSet);
        handler.post(() -> copyLocalToRemoteOverrideTask.run());
        refreshRemoteSdvFiles();
    }

    private void copyRemoteToLocalIncreaseIndex(SdvFileSet sdvFileSet) {
        fetchButton.setEnabled(false);
        copyRemoteToLocalIncreaseIndexTask.setSource(sdvFileSet);
        handler.post(() -> copyRemoteToLocalIncreaseIndexTask.run());
        refreshRemoteSdvFiles();
    }

    private void copyRemoteToLocalOverride(SdvFileSet sdvFileSet) {
        fetchButton.setEnabled(false);
        copyRemoteToLocalOverrideTask.setSource(sdvFileSet);
        handler.post(() -> copyRemoteToLocalOverrideTask.run());
        refreshRemoteSdvFiles();
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
        handler.post(() -> remoteSdvFileSetsRefreshTask.run());
    }

}