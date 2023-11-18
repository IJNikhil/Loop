package com.loop.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loop.app.model.UserModel;
import com.loop.app.utils.AndroidUtil;
import com.loop.app.utils.FirebaseUtil;

public class ProfileActivity extends AppCompatActivity {


    // Declare the views
    ImageView profilePic, backBtn;
    EditText usernameInput;
    TextView phoneInput;
    Button updateProfileBtn;
    private LinearProgressIndicator progressBar;
    TextView logoutBtn;

    // Declare the UserModel object
    UserModel currentUserModel;
    Uri selectedImageUri;


    // Declare the ActivityResultLauncher
    ActivityResultLauncher<Intent> imagePickLauncher;
    View screenView;

    public static final int REQUEST_CODE_PICK_IMAGE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        phoneInput = findViewById(R.id.profile_phone);
        updateProfileBtn = findViewById(R.id.profile_update_btn);
        progressBar = findViewById(R.id.profileProgressBar);
        logoutBtn = findViewById(R.id.logout_btn);
        backBtn = findViewById(R.id.back_btn);

        screenView = getWindow().getDecorView().getRootView();

        // Get the current user data
        getUserData();

        backBtn.setOnClickListener(view -> {onBackPressed();});

        // Set the click listener for the update profile button
        updateProfileBtn.setOnClickListener(v -> updateBtnClick());

        // Set the click listener for the logout button
        logoutBtn.setOnClickListener(v -> FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUtil.logout();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } ));

        // Register for the image pick activity result
        // Register for the image pick activity result
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getApplicationContext(), selectedImageUri, profilePic);
                        }
                    }
                });

                        // Set the click listener for the profilePic view
        profilePic.setOnClickListener(v -> {
            // Start the default system image picker activity
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickLauncher.launch(intent);
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                profilePic.setImageURI(selectedImageUri);
            }
        }
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 char");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);

        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageReference().putFile(selectedImageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // The image was uploaded successfully
                    updateToFirestore();
                } else {
                    // The image upload failed
                    setInProgress(false);
                    AndroidUtil.showToast(screenView, "Image upload failed");
                }
            });
        } else {
            // The user did not select an image
            updateToFirestore();
        }
    }

    void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);

                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(screenView, "Updated successfully");
                    } else {
                        AndroidUtil.showToast(screenView, "Updated failed");
                    }
                } );
    }

    void getUserData() {
        setInProgress(true);

        FirebaseUtil.getCurrentProfilePicStorageReference().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getApplicationContext(), uri, profilePic);

                    }
                } );
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            usernameInput.setText(currentUserModel.getUsername());
            phoneInput.setText(currentUserModel.getPhone());
        } );
    }


    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}