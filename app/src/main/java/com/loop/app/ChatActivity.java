package com.loop.app;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.loop.app.adapter.ChatRecyclerAdapter;
import com.loop.app.model.ChatMessageModel;
import com.loop.app.model.ChatroomModel;
import com.loop.app.model.UserModel;
import com.loop.app.utils.AndroidUtil;
import com.loop.app.utils.FirebaseUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChatActivity extends AppCompatActivity {
    ImageView backBtn;
    UserModel otherUser;
    String chatroomId;
    EditText msgInput;
    ImageView sendMsgBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        msgInput = findViewById(R.id.chat_msg_input);
        sendMsgBtn = findViewById(R.id.msg_send_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        backBtn = findViewById(R.id.back_btn);
        imageView = findViewById(R.id.profile_pic_image_view);

        FirebaseUtil.getOtherProfilePicStorageReference(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this, uri, imageView);

                    }
                } );

        backBtn.setOnClickListener(view -> onBackPressed());



        otherUsername.setText(otherUser.getUsername());
        sendMsgBtn.setOnClickListener(view -> {
            String message = msgInput.getText().toString().trim();
            if (message.isEmpty()) {
                return;
            }
            sendMsgToUser(message);
        });
        getOrCreateChatroomModel();

        setUpChatRecyclerView();

    }

    private void setUpChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);

        recyclerView.setLayoutManager(manager);

//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();


        // For Smooth Chat Msg Transaction
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMsgToUser(String message) {

        chatroomModel.setLastMsgTimeStamp(Timestamp.now());
        chatroomModel.setLastMsgSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMsg(message);

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        msgInput.setText("");
                        sendNotification(message);
                    }
                });
    }

    void sendNotification(String message) {
        // Current username, message, currentUserId, otheruserToken
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("title", currentUser.getUsername());
                    notificationObject.put("body", message);

                    JSONObject dataObject = new JSONObject();
                    dataObject.put("userId", currentUser.getUserId());

                    jsonObject.put("notification", notificationObject);
                    jsonObject.put("data", dataObject);
                    jsonObject.put("to", otherUser.getFcmToken());

                    callApi(jsonObject);
                } catch (Exception e) {

                }
            }
        } );
    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAh-fUu6E:APA91bGfMYMf1_aLy1toOwXI_8BGFK1fT2TvYlRkPSbIOzDjHqaHva3SERrmuuEQIW-hvBQ2dSuAIDg9iVGIsAZXCgKLQdKZwkI_DzgzZgbSfle6B54b2kgv0jErr5zpVO876aCrqEBa")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    private void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    //first time chat

                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        } );
    }
}