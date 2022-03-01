package com.example.studentportal.modelClasses;

import java.io.Serializable;

public class PostUploadModel implements Serializable {

    private String postText;
    private String userId;
    private String postId;
    private String imageUrl;

    public PostUploadModel(){}

    public PostUploadModel(String postText, String userId, String postId, String imageUrl) {
        this.postText = postText;
        this.userId = userId;
        this.postId = postId;
        this.imageUrl = imageUrl;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
