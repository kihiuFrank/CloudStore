package com.example.cloudstore;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseStorage storage;
    public static StorageReference storageReference;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public static ArrayList<FileInfo> fileInfos;
    private static final int RC_SIGN_IN = 123;
    private static Activity caller;

    private FirebaseUtil(){}

    public static void openFbReference (String ref, final Activity callerActivity) {
        if (firebaseUtil == null) {
            firebaseUtil =new FirebaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser()==null) {
                        FirebaseUtil.signUp();
                    }
                    //Toast.makeText(callerActivity.getBaseContext(), "Welcome!!", Toast.LENGTH_SHORT).show();
                }
            };
            connectStorage();
        }
        fileInfos = new ArrayList<FileInfo>();
        databaseReference = firebaseDatabase.getReference().child(ref);
    }

    public static void signUp() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachAuthListener() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static void dettachAuthListener() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    public static void connectStorage() {
        storage = FirebaseStorage.getInstance();
        storageReference= storage.getReference().child("encrypted_files");
    }
}
