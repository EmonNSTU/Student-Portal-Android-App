package com.example.studentportal.modelClasses;

public class UserPostModel {
    String batch; //local
    String created_at;
    String id;
    String image_url;
    String post;
    String user_id; // local
    String user_name; // local
    String user_profile_img; // local
    UserLike like;
    UserComment comment;

    public UserPostModel() {}

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profile_img() {
        return user_profile_img;
    }

    public void setUser_profile_img(String user_profile_img) {
        this.user_profile_img = user_profile_img;
    }

    public UserLike getLike() {
        return like;
    }

    public void setLike(UserLike like) {
        this.like = like;
    }

    public UserComment getComment() {
        return comment;
    }

    public void setComment(UserComment comment) {
        this.comment = comment;
    }

    class UserLike {
        String status;
        String user_id;

        public UserLike(){}

        public UserLike(String status, String user_id) {
            this.status = status;
            this.user_id = user_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }

    class UserComment {
        String comment_text;
        String created_at;
        String user_id;

        public UserComment() {}

        public UserComment(String comment_text, String created_at, String user_id) {
            this.comment_text = comment_text;
            this.created_at = created_at;
            this.user_id = user_id;
        }

        public String getComment_text() {
            return comment_text;
        }

        public void setComment_text(String comment_text) {
            this.comment_text = comment_text;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }

}
