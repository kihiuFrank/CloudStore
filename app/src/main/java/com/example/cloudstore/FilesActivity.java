package com.example.cloudstore;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class FilesActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    EditText textTitle, textDescription;
    FileInfo fileInfo;

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

        Intent intent = getIntent();
        FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("Files");

        if (fileInfo==null) {
            fileInfo = new FileInfo();
        }
        this.fileInfo = fileInfo;
        textTitle.setText(fileInfo.getTitle());
        textDescription.setText(fileInfo.getDescription());

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

}
