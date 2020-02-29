package com.example.cloudstore;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class FilesActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    EditText textTitle, textDescription;
    private static final int FILE_RESULT = 42;
    FileInfo fileInfo;
    Button btnEncrypt, btnDecrypt;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FirebaseUtil.openFbReference("encryptedFiles");
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;

        textTitle = findViewById(R.id.tvTitle);
        textDescription = findViewById(R.id.tvDescription);
        btnEncrypt = findViewById(R.id.btn_upload);
        btnDecrypt = findViewById(R.id.btn_read);
        imageView = findViewById(R.id.image);


        Intent intent = getIntent();
        FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("Files");

        if (fileInfo==null) {
            fileInfo = new FileInfo();
        }
        this.fileInfo = fileInfo;
        textTitle.setText(fileInfo.getTitle());
        textDescription.setText(fileInfo.getDescription());
        showImage(fileInfo.getFileUrl());


        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true) ;
                startActivityForResult(Intent.createChooser(intent,
                        "Insert File"), FILE_RESULT);
            }
        });

        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        btnEncrypt.setEnabled(true);
                        btnDecrypt.setEnabled(true);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(FilesActivity.this, "You must enable permissions", Toast.LENGTH_SHORT).show();
                    }
                }).check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_RESULT ) {
            Uri fileUri = data.getData();
            final StorageReference reference = FirebaseUtil.storageReference.child(fileUri.getLastPathSegment());
            reference.putFile(fileUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
/*
                    String url = taskSnapshot.getStorage().getDownloadUrl().toString();
                    fileInfo.setFileUrl(url);
                    showImage(url);*/
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            fileInfo.setFileUrl(uri.toString());
                            showImage(uri.toString());
                        }
                    });

                }
            });
        }

        if (resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            final StorageReference reference = FirebaseUtil.storageReference.child(fileUri.getLastPathSegment());
            reference.putFile(fileUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

/*                    String url = taskSnapshot.getStorage().getDownloadUrl().toString();
                    fileInfo.setFileUrl(url);
                    showImage(url);*/
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            fileInfo.setFileUrl(uri.toString());
                            showImage(uri.toString());
                        }
                    });

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (id) {
            case R.id.save_menu:
                saveFile();
                Toast.makeText(this, "File Saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteFile();
                Toast.makeText(this, "File Info Deleted",Toast.LENGTH_SHORT).show();
                backToList();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void saveFile() {
        fileInfo.setTitle(textTitle.getText().toString());
        fileInfo.setDescription(textDescription.getText().toString());

        if (fileInfo.getId() == null) {
            databaseReference.push().setValue(fileInfo);
        } else {
            databaseReference.child(fileInfo.getId()).setValue(fileInfo);
        }

    }

    private void deleteFile() {
        if (fileInfo==null) {
            Toast.makeText(this, "Please save the File Info before deleting",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.child(fileInfo.getId()).removeValue();
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent );
    }

    private void clean() {

        textTitle.setText("");
        textDescription.setText("");

        textTitle.requestFocus();
    }

    private void showImage(String url) {
        if (url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }


}
