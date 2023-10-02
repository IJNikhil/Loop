package com.loop.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loop.app.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }

    private void setListener() {
        binding.textCreateNewAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
//        binding.buttonSignIn.setOnClickListener(v -> addDataToFirestore());
        binding.buttonSignIn.setOnClickListener(v -> signInAction());
    }
    

    private void signInAction() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

//    private void addDataToFirestore() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("First_name", "Vighnesh");
//        data.put("Last_name", "Badganchi");
//
//        db.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference -> Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
//    }


}