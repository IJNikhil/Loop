package com.loop.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    EditText phoneInput;
    CountryCodePicker ccp;
    Button sendOtpBtn;
    private LinearProgressIndicator progressBar;
    View screenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        initViews();

        progressBar.setVisibility(View.GONE);

        sendOtpBtn.setOnClickListener((v) -> {
            if (!ccp.isValidFullNumber()) {
                phoneInput.setError("Phone number not valid");
                return;
            }

            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("phone", ccp.getFullNumberWithPlus());
            startActivity(intent);
        });
    }

    private void initViews() {
        ccp = findViewById(R.id.ccp);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.loginPhoneProgressBar);

        ccp.registerCarrierNumberEditText(phoneInput);
    }
}