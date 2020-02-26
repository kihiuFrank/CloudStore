package com.example.cloudstore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder>{

    ArrayList<FileInfo> arrayFileInfo;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;


    public FilesAdapter() {
        //FirebaseUtil.openFbReference("encryptedFiles");
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        arrayFileInfo = FirebaseUtil.fileInfos;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FileInfo fileInfo = dataSnapshot.getValue(FileInfo.class);
                Log.d("File", fileInfo.getTitle() );
                fileInfo.setId(dataSnapshot.getKey());
                arrayFileInfo.add(fileInfo);
                notifyItemInserted(arrayFileInfo.size()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false);
        return new FilesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesViewHolder holder, int position) {
        FileInfo fileInfo = arrayFileInfo.get(position);
        holder.bind(fileInfo);
    }

    @Override
    public int getItemCount() {
        return arrayFileInfo.size();
    }

    public class FilesViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
        TextView tvTitle;
        TextView tvDescription;

        public FilesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            itemView.setOnClickListener(this);
        }

        public void bind(FileInfo fileInfo) {
              tvTitle.setText(fileInfo.getTitle());
              tvDescription.setText(fileInfo.getDescription());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            FileInfo selectedFile = arrayFileInfo.get(position);
            Intent intent = new Intent(v.getContext(), FilesActivity.class);
            intent.putExtra("Files", selectedFile);
            v.getContext().startActivity(intent);
        }
    }
}
