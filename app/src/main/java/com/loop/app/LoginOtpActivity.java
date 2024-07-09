package com.loop.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.loop.app.utils.AndroidUtil;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOtpActivity extends AppCompatActivity {

    String phoneNumber, verificationCode;
    Long timeoutSeconds = 30L;
    EditText otpInput;
    ImageView backBtn;
    LinearProgressIndicator progressBar;
    MaterialTextView resendOtpTextView, displayTextView;
    MaterialButton nextBtn;
    View sView;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");

        setContentView(R.layout.activity_login_otp);
        initializeViews();

        setInProgress(false);
        sendOtp(phoneNumber, false);

        backBtn.setOnClickListener(view -> onBackPressed());

        nextBtn.setOnClickListener(v -> {
            String enteredOtp = otpInput.getText().toString();
            if (!enteredOtp.isEmpty()) {
                setInProgress(true);
                resendOtpTextView.setVisibility(View.INVISIBLE);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                signIn(credential);
            } else {
                otpInput.setError("Enter OTP First");
            }
        });
        resendOtpTextView.setOnClickListener(view -> sendOtp(phoneNumber, true));
    }

    public void sendOtp(String phoneNumber, Boolean isResend) {
        // Set the progress bar to indicate that the OTP is being sent.
        resendOtpTextView.setVisibility(View.GONE);
        setInProgress(true);
        nextBtn.setVisibility(View.INVISIBLE);

        displayTextView.setText("Please wait while OTP is sending on entered number " + phoneNumber);
        // Create a PhoneAuthOptions object with the specified phone number, timeout, and activity.
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Sign the user in with the phone auth credential.
                        signIn(phoneAuthCredential);
                        // Set the progress bar to indicate that the operation is complete.
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // Show a toast message indicating that the OTP was not sent.
                        AndroidUtil.showToast(sView , "OTP not sent");
                        // Set the progress bar to indicate that the operation is complete.
                        setInProgress(false);
                        // Start the login phone number activity.
                        Intent intent = new Intent(LoginOtpActivity.this, LoginPhoneNumberActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // Start the resend timer.
                        setInProgress(false);
                        startResendTimer();
                         // Save the verification code and force resending token.
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        // Show a toast message indicating that the OTP was sent successfully.
                        displayTextView.setText("Enter the OTP sent on number " + phoneNumber);
                        nextBtn.setVisibility(View.VISIBLE);
                        AndroidUtil.showToast(sView, "OTP Sent Successfully");
                        // Set the progress bar to indicate that the operation is complete.
                        setInProgress(false);
                    }
                });

        // If the OTP is being resent, use the force resending token. Otherwise, don't use it.
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

//    void signIn(PhoneAuthCredential phoneAuthCredential) {
//        setInProgress(true);
//        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
//            setInProgress(false);
//            if (task.isSuccessful()) {
//                Intent intent = new Intent(LoginOtpActivity.this, LoginUserNameActivity.class);
//                intent.putExtra("phone", phoneNumber);
//                startActivity(intent);
//            } else {
//                View view = getWindow().getDecorView().getRootView();
//                AndroidUtil.showToast(view, "OTP Verification Failed");
//            }
//        });
//    }


        void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
            Intent intent = new Intent(LoginOtpActivity.this, LoginUserNameActivity.class);
            intent.putExtra("phone", phoneNumber);
            startActivity(intent);
            } else {
                AndroidUtil.showToast(sView, "OTP Verifiaction Failed!");
            }
        });
    }

    void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        resendOtpTextView.setVisibility(View.VISIBLE);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                timeoutSeconds--;
                resendOtpTextView.setText("Resend OTP in " + timeoutSeconds + " Seconds");
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 30L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtpTextView.setEnabled(true);
                        resendOtpTextView.setText("Click to resend OTP");
                    });
                }
            }
        }, 0, 1000);
    }
    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }
    private void initializeViews() {
        backBtn = findViewById(R.id.backbtn);
        otpInput = findViewById(R.id.login_otp);
        nextBtn = findViewById(R.id.login_next_btn);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);
        progressBar = findViewById(R.id.loginOtpProgressBar);
        displayTextView = findViewById(R.id.number_textview);

        sView = getWindow().getDecorView().getRootView();
        AndroidUtil.showToast(sView, phoneNumber);
    }
}