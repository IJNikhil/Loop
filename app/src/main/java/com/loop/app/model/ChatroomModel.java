package com.loop.app.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {
    String chatroomId;
    List<String> userId;
    Timestamp lastMsgTimeStamp;
    String lastMsgSenderId;
    String lastMsg;

    public ChatroomModel() {}

    public ChatroomModel(String chatroomId, List<String> userId, Timestamp lastMsgTimeStamp, String lastMsgSenderId) {
        this.chatroomId = chatroomId;
        this.userId = userId;
        this.lastMsgTimeStamp = lastMsgTimeStamp;
        this.lastMsgSenderId = lastMsgSenderId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }

    public Timestamp getLastMsgTimeStamp() {
        return lastMsgTimeStamp;
    }

    public void setLastMsgTimeStamp(Timestamp lastMsgTimeStamp) {
        this.lastMsgTimeStamp = lastMsgTimeStamp;
    }

    public String getLastMsgSenderId() {
        return lastMsgSenderId;
    }

    public void setLastMsgSenderId(String lastMsgSenderId) {
        this.lastMsgSenderId = lastMsgSenderId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}
