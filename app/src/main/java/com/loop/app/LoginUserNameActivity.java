package com.loop.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.UploadTask;
import com.loop.app.model.UserModel;
import com.loop.app.utils.AndroidUtil;
import com.loop.app.utils.FirebaseUtil;

import java.util.Objects;

public class LoginUserNameActivity extends AppCompatActivity {

    TextInputEditText usernameInput;
    ImageView profilePic;
    MaterialButton letMeInBtn;
    private LinearProgressIndicator progressBar;
    String phoneNumber;
    MaterialTextView nameTextView;
    UserModel userModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    public static final int REQUEST_CODE_PICK_IMAGE = 1;
    View screenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_name);

        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");

        profilePic = findViewById(R.id.profile_image_view);
        nameTextView = findViewById(R.id.name_textview);
        usernameInput = findViewById(R.id.login_username);
        letMeInBtn = findViewById(R.id.login_letMeIn_btn);
        progressBar = findViewById(R.id.loginUsernameProgressBar);

        screenView = getWindow().getDecorView().getRootView();


        getUsername();

        letMeInBtn.setOnClickListener((v -> {
            setUsername();
        }));


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

//    void setUsername() {
//        String username = Objects.requireNonNull(usernameInput.getText()).toString();
//        if (username.isEmpty() || username.length() < 3) {
//            usernameInput.setError("Username length should be at least 3 char");
//            return;
//        }
//        setInProgress(true);
//
//        if (userModel != null) {
//            userModel.setUsername(username);
//
//        } else {
//            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId());
//        }
//
//
//        if (selectedImageUri != null) {
//            // Handle image upload
//            FirebaseUtil.getCurrentProfilePicStorageReference().putFile(selectedImageUri).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    // Image uploaded successfully, proceed to update user data
//                    updateToFirestore();
//                } else {
//                    // Image upload failed, notify user and set inProgress to false
//                    setInProgress(false);
//                    AndroidUtil.showToast(screenView, "Image upload failed");
//                }
//            });
//        } else {
//            // No image selected, proceed directly to update user data
//            updateToFirestore();
//        }
//
//    }
    void getUsername() {
        setInProgress(true);

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Data fetched successfully, update UI and set inProgress to false
                userModel = task.getResult().toObject(UserModel.class);

                if (userModel != null) {
                    usernameInput.setText(userModel.getUsername());
                }
//                setInProgress(false);
            } else {
                // Data fetching failed, notify user and set inProgress to false
                setInProgress(false);
                AndroidUtil.showToast(screenView, "Failed to get user data: " + task.getException().getMessage());
            }
        });


        FirebaseUtil.getCurrentProfilePicStorageReference().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        // Check if URL is null
                        if (uri != null) {
                            // Download and set profile picture
                            AndroidUtil.setProfilePic(getApplicationContext(), uri, profilePic);
                        } else {
                            // Profile picture not found, notify user
                            AndroidUtil.showToast(screenView, "Profile picture not found");
                        }
                    } else {
                        // Failed to retrieve profile picture URL
                        setInProgress(false);
//                        AndroidUtil.showToast(screenView, "Failed to get profile picture: " + task.getException().getMessage());
                    }

                });



//        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
//            setInProgress(false);
//            if (task.isSuccessful() && task.getResult() != null) {
//                userModel = task.getResult().toObject(UserModel.class);
//                if (userModel != null) {
//                    usernameInput.setText(userModel.getUsername());
//                }
//            } else {
//                // Handle the case where the task is not successful or getResult() is null
//            }
//
//        });

    }

    void setUsername() {
        String username = Objects.requireNonNull(usernameInput.getText()).toString();

        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Username length should be at least 3 char");
            return;
        }

        setInProgress(true);

        if (userModel != null) {
            userModel.setUsername(username);
        } else {
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId());
        }


        if (selectedImageUri != null) {
            // Handle image upload on a background thread
            new Thread(() -> {
                FirebaseUtil.getCurrentProfilePicStorageReference().putFile(selectedImageUri)
                        .addOnCompleteListener(task -> {
                            runOnUiThread(() -> {
                                if (task.isSuccessful()) {
                                    updateToFirestore();
                                } else {
                                    // Image upload failed, notify user and set inProgress to false
                                    setInProgress(false);
                                    AndroidUtil.showToast(screenView, "Image upload failed");
                                }
                            });
                        });
            }).start();
        }

                updateToFirestore();




//        if (selectedImageUri != null) {
//            // Handle image upload on a background thread
//            new Thread(() -> {
//                FirebaseUtil.getCurrentProfilePicStorageReference().putFile(selectedImageUri)
//                        .addOnCompleteListener(task -> {
//                            runOnUiThread(() -> handleImageUploadResult(task));
//                        });
//            }).start();
//        } else {
//            // No image selected, proceed directly to update user data
//            updateToFirestore();
//        }
    }



    private void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginUserNameActivity.this, DashBoard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
}