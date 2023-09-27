package com.loop.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.loop.app.R;
import com.loop.app.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setListener();

    }

    private void setListener() {
        binding.textSignIn.setOnClickListener(v -> onBackPressed());

    }
}