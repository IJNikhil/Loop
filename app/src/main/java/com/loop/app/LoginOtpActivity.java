package com.loop.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loop.app.utils.AndroidUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOtpActivity extends AppCompatActivity {

    String phoneNumber;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    Long timeoutSeconds = 30L;
    EditText otpInput;
    ProgressBar progressBar;
    TextView resendOtpTextView;
    Button nextBtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        phoneNumber = getIntent().getExtras().getString("phone");

        otpInput = findViewById(R.id.login_otp);
        nextBtn = findViewById(R.id.login_next_btn);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);
        progressBar = findViewById(R.id.login_progress_bar);

        AndroidUtil.showToast(getApplicationContext(), phoneNumber);


        sendOtp(phoneNumber, false);

        nextBtn.setOnClickListener(v -> {
            String enteredOtp = otpInput.getText().toString();
            if (!enteredOtp.isEmpty()) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                signIn(credential);
                setInProgress(true);
            } else {
                otpInput.setError("Enter OTP First");
            }
        });

        resendOtpTextView.setOnClickListener(view -> sendOtp(phoneNumber, true));
    }

    public void sendOtp(String phoneNumber, boolean isResend) {
        // Set the progress bar to indicate that the OTP is being sent.
        resendOtpTextView.setText("OTP is Sending..");
        setInProgress(true);

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
                        AndroidUtil.showToast(getApplicationContext(), "OTP not sent");

                        // Set the progress bar to indicate that the operation is complete.
                        setInProgress(false);

                        // Start the login phone number activity.
                        Intent intent = new Intent(LoginOtpActivity.this, LoginPhoneNumberActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // Start the resend timer.
                        startResendTimer();

                        // Save the verification code and force resending token.
                        verificationCode = s;
                        resendingToken = forceResendingToken;

                        // Show a toast message indicating that the OTP was sent successfully.
                        AndroidUtil.showToast(getApplicationContext(), "OTP Sent Successfully");

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


    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginOtpActivity.this, LoginUserNameActivity.class);
                intent.putExtra("phone", phoneNumber);
                startActivity(intent);
            } else {
                AndroidUtil.showToast(getApplicationContext(), "OTP Verification Failed");
            }
        });
    }

    void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
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
//                        status = 1;
                    });
                }
            }
        }, 0, 1000);
    }
}