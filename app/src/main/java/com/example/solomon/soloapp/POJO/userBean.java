package com.example.solomon.soloapp.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class userBean {

    @SerializedName("userpin_available")
    @Expose
    private Boolean userpinAvailable;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getUserpinAvailable() {
        return userpinAvailable;
    }

    public void setUserpinAvailable(Boolean userpinAvailable) {
        this.userpinAvailable = userpinAvailable;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
