package com.loop.app.model;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String msg;
    private String senderId;
    private Timestamp timestamp;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String msg, String senderId, Timestamp timestamp) {
        this.msg = msg;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
