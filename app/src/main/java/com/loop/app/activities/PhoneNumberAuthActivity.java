package com.loop.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.loop.app.MainActivity;
import com.loop.app.databinding.ActivityPhoneNumberAuthBinding;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneNumberAuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    ActivityPhoneNumberAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPhoneNumberAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Initialize the callbacks for phone verification
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // Automatic verification or instant validation
                // You can sign in the user here.
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // Verification failed, handle the error.
                Toast.makeText(PhoneNumberAuthActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number.
                // You can now prompt the user to enter the code.
                PhoneNumberAuthActivity.this.verificationId = verificationId;
                Toast.makeText(PhoneNumberAuthActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
            }
        };


        binding.getOTP.setOnClickListener(view -> {
            String phoneNumber = binding.inputPhoneNumber.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                // Start the phone number verification process.
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        callbacks);
            } else {
                Toast.makeText(PhoneNumberAuthActivity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonVerify.setOnClickListener(view -> {
            String otp = binding.inputOTP.getText().toString().trim();
            if (!otp.isEmpty()) {
                // Verify the OTP code with Firebase.
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(PhoneNumberAuthActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI.
                        Toast.makeText(PhoneNumberAuthActivity.this, "Authentication successful!", Toast.LENGTH_SHORT).show();
                        // Redirect to the main chat activity or another screen.
                        Intent intent = new Intent(PhoneNumberAuthActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close this authentication activity.
                    } else {
                        // Sign in failed, display a message to the user.
                        Toast.makeText(PhoneNumberAuthActivity.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
