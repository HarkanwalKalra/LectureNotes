package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class FileDisplayActivity extends AppCompatActivity {

    private TextView fileName, courseName, fileSize;
    private ImageView downloadButton, viewButton, shareButton, bookmarkButton;
    private FileDetails fileDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display);

        createToolbar();
        setIDs();
        onClickListeners();
        getFileDetails();
        displayFileDetails();
    }

    private void displayFileDetails() {
        fileName.setText(fileDetails.getFileName());
        courseName.setText(fileDetails.getSubjectName());
        fileSize.setText(fileDetails.getFileSize());
    }

    private void getFileDetails() {

        Gson gson = new Gson();

        String fileAsJSON = getIntent().getStringExtra("file_details");
        fileDetails = gson.fromJson(fileAsJSON, FileDetails.class);

    }

    private void setIDs() {

        fileName = findViewById(R.id.file_name);
        courseName = findViewById(R.id.course_name);
        fileSize = findViewById(R.id.file_size);

        viewButton = findViewById(R.id.view_button);
        downloadButton = findViewById(R.id.download_button);
        shareButton = findViewById(R.id.share_button);
        bookmarkButton = findViewById(R.id.bookmark_button);
    }

    private void onClickListeners() {

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Viewing", Toast.LENGTH_SHORT).show();
            }
        });
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile2();
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sharing", Toast.LENGTH_SHORT).show();
            }
        });
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Bookmarked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void downloadFile2() {

        DownloadManager downloadmanager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(fileDetails.getFileUrl());
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalFilesDir(this, DIRECTORY_DOWNLOADS, fileDetails.getFileName() + ".pdf");
        downloadmanager.enqueue(request);
    }


    void downloadFile() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(fileDetails.getFileUrl());

        File rootPath = new File(getFilesDir(),"notesApp");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File myFile = new File(rootPath,fileDetails.getFileName()+".pdf");

        storageRef.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e("Download Failed: ",exception.toString());
            }
        });

    }


    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
