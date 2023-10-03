package com.niazi.chatsapp.Model_Classes;

public class Message {

    private String messageId, message, sender_id, imageUrl;

    private long timesnap;

    private int feeling = -1;

    public Message() {
    }

    public Message(String message, String sender_id, long timesnap) {
        this.message = message;
        this.sender_id = sender_id;
        this.timesnap = timesnap;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public long getTimesnap() {
        return timesnap;
    }

    public void setTimesnap(long timesnap) {
        this.timesnap = timesnap;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

}