package com.loop.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.auth.User;
import com.loop.app.function.DisplayTag;
import com.loop.app.model.UserModel;
import com.loop.app.utils.AndroidUtil;
import com.loop.app.utils.FirebaseUtil;


public class MainActivity extends AppCompatActivity {

    private TextView splashTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        splashTag = findViewById(R.id.splashtag);
        View screenView;

//
//        if (getIntent().getExtras() != null) {
//            String userId = getIntent().getExtras().getString("userId");
//            FirebaseUtil.allUserCollectionReference().document(userId).get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    UserModel userModel = task.getResult().toObject(UserModel.class);
//                    Intent intent = new Intent(this, ChatActivity.class);
//                    AndroidUtil.passUserModelAsIntent(intent, userModel);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//
//                }
//            });
//        } else {
//            new Handler().postDelayed(() -> {
//                if (FirebaseUtil.isLoggedIn()) {
//                    startActivity(new Intent(MainActivity.this, DashBoard.class));
//                } else {
//                    startActivity(new Intent(MainActivity.this, LoginPhoneNumberActivity.class));
//                }
//                finish();
//            }, 1000);
//        }

        if (FirebaseUtil.isLoggedIn() && getIntent().getExtras() != null) {
//            from Notification
            String userId = getIntent().getStringExtra("userId");
            FirebaseUtil.allUserCollectionReference().document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    Intent intent = new Intent(this, ChatActivity.class);
                    AndroidUtil.passUserModelAsIntent(intent, userModel);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

//                    Intent intent = new Intent(this, ChatActivity.class);
//
//                    AndroidUtil.passUserModelAsIntent(intent, model);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
                }
            } );

        } else {
            new Handler().postDelayed(() -> {
                if (FirebaseUtil.isLoggedIn()) {
                    startActivity(new Intent(MainActivity.this, DashBoard.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginPhoneNumberActivity.class));
                }
                finish();
            }, 1000);
        }



    }

}


















//
//package com.loop.app;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.loop.app.function.DisplayTag;
//import com.loop.app.model.UserModel;
//import com.loop.app.utils.AndroidUtil;
//import com.loop.app.utils.FirebaseUtil;
//
//public class MainActivity extends AppCompatActivity {
//
//    private TextView splashTag;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_main);
//
//        splashTag = findViewById(R.id.splashtag);
//        View screenView;
//
//        if (FirebaseUtil.isLoggedIn() && getIntent().getExtras() != null) {
//            // from Notification
//            String userId = getIntent().getStringExtra("userId");
//
//            // Perform null check for userId
//            if (userId != null) {
//                FirebaseUtil.allUserCollectionReference().document(userId).get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        UserModel userModel = task.getResult().toObject(UserModel.class);
//                        Intent intent = new Intent(this, ChatActivity.class);
//                        AndroidUtil.passUserModelAsIntent(intent, userModel);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    } else {
//                        // Handle the case where the task is not successful
//                        // For example, show a toast or log an error message
//                        Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                // Handle the case where userId is null
//                // For example, show a toast or log a message
//                Toast.makeText(MainActivity.this, "UserId is null", Toast.LENGTH_SHORT).show();
//            }
//
//        } else {
//            new Handler().postDelayed(() -> {
//                if (FirebaseUtil.isLoggedIn()) {
//                    startActivity(new Intent(MainActivity.this, DashBoard.class));
//                } else {
//                    startActivity(new Intent(MainActivity.this, LoginPhoneNumberActivity.class));
//                }
//                finish();
//            }, 1000);
//        }
//    }
//}
