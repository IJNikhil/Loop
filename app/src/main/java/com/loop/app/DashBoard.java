package com.loop.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loop.app.adapter.RecentChatRecyclerAdapter;
import com.loop.app.model.ChatroomModel;
import com.loop.app.utils.FirebaseUtil;

public class DashBoard extends AppCompatActivity {

    ExtendedFloatingActionButton searchButton;
    ImageView profileBtn;
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        searchButton = findViewById(R.id.search_main_btn);
        recyclerView = findViewById(R.id.recent_user_recycler_view);
        profileBtn = findViewById(R.id.main_profile_btn);

        setupRecyclerView();

        getFCMToken();

        searchButton.setOnClickListener((v -> {
            startActivity(new Intent(DashBoard.this, SearchUserActivity.class));
        }));


        profileBtn.setOnClickListener((v -> {
            startActivity(new Intent(DashBoard.this, ProfileActivity.class));
        }));

    }


    void setupRecyclerView() {

        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userId", FirebaseUtil.currentUserId())
                .orderBy("lastMsgTimeStamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken", token);
            }
        } );
    }
}