package com.example.android.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.OpenableColumns;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static final int PICK_FILE_REQUEST = 1;
    private FloatingActionButton floatingActionButton;
    private Uri uploadfileuri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String downloadURL;
    private UploadTask uploadTask;
    private Context mContext;
    private FragmentActivity mActivity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setIDs();
        onClickListeners();
    }

    private void setIDs() {

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        floatingActionButton = getActivity().findViewById(R.id.floatingActionButton);
    }

    private void onClickListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseFile();
            }
        });
    }

    private void browseFile() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_FILE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FILE_REQUEST && data != null) {

            if (resultCode == RESULT_OK && data.getData() != null) {

                uploadfileuri = data.getData();
                showDialogBox();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDialogBox() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);

        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.upload_dialog, viewGroup, false);
        builder.setView(dialogView);

        final Spinner courseName = dialogView.findViewById(R.id.course_name_spinner);

        String[] courses = {"BCA", "MBA", "BCom", "BE"};
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, courses);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseName.setAdapter(aa);
        courseName.setSelection(0);

        final EditText fileName = dialogView.findViewById(R.id.file_name_edittext);
        MaterialButton uploadButton = dialogView.findViewById(R.id.upload_button);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancel_button);

        final AlertDialog alertDialog = builder.create();
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    String fileSize = getFileSize();
                    if (fileName.equals("largeFile")) {
                        Toast.makeText(getActivity(), "Cannot upload large files", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getActivity(), "Uploading File", Toast.LENGTH_SHORT).show();
                    uploadFile(fileName.getText().toString().trim(), courseName.getSelectedItem().toString(), fileSize);
                    alertDialog.dismiss();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadFile(final String fileName, final String courseName, final String fileSize) {
        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".pdf");
        uploadTask = fileReference.putFile(uploadfileuri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
/*
                downloadURL = fileReference.getDownloadUrl().toString();
                FileDetails uploadFile = new FileDetails(fileName, courseName, downloadURL, fileSize);
                String uploadID = databaseReference.push().getKey();
                databaseReference.child(uploadID).setValue(uploadFile);
                Toast.makeText(getActivity(), "File Uploaded", Toast.LENGTH_SHORT).show();
*/
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadURL = uri.toString();
                        FileDetails uploadFile = new FileDetails(fileName, courseName, downloadURL, fileSize);
                        String uploadID = databaseReference.push().getKey();
                        databaseReference.child(uploadID).setValue(uploadFile);
                        Toast.makeText(getActivity(), "File Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFileSize() {

        Cursor cursor = getActivity().getContentResolver().query(uploadfileuri, null, null, null, null);
        cursor.moveToFirst();
        long fileSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
        cursor.close();

        String hrSize = "";
        fileSize = fileSize / 1024;
        int m = (int) fileSize / (1024 * 1024);
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(fileSize).concat("KB");
        }
        if (m > 10) {
            hrSize = "largeFile";
        }
        return hrSize;

    }

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mContext = context;
            mActivity = getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
