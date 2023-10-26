package com.loop.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.loop.app.function.DisplayTag;
import com.loop.app.model.UserModel;
import com.loop.app.utils.AndroidUtil;
import com.loop.app.utils.FirebaseUtil;

public class MainActivity extends AppCompatActivity {

    private TextView splashTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user has already granted the READ_MEDIA_IMAGES permission
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
//            // Request the READ_MEDIA_IMAGES permission from the user
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
//        }

        setContentView(R.layout.activity_main);

        initUi();

//        new Handler().postDelayed(() -> {
//            Intent intent = new Intent(MainActivity.this, LoginPhoneNumberActivity.class);
//            startActivity(intent);
//            finish();
//            Toast.makeText(splashTag.getContext(), "Completed", Toast.LENGTH_SHORT).show();
//        }, 1100);




        if (FirebaseUtil.isLoggedIn() && getIntent().getExtras() != null) {
//            from Notification
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    UserModel model = task.getResult().toObject(UserModel.class);
                    Intent mainIntent = new Intent(this, DashBoard.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(mainIntent);

                    Intent intent = new Intent(this, ChatActivity.class);

                    AndroidUtil.passUserModelAsIntent(intent, model);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } );

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FirebaseUtil.isLoggedIn()) {
                        startActivity(new Intent(MainActivity.this, DashBoard.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginPhoneNumberActivity.class));
                    }
                    finish();
                }
            }, 1000);
        }



    }



    private void initUi() {
        splashTag = findViewById(R.id.splashtag);
    }
}