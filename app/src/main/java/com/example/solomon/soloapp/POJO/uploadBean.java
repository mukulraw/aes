package com.example.solomon.soloapp.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class uploadBean {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("encrpted_id")
    @Expose
    private Integer encrptedId;
    @SerializedName("message")
    @Expose
    private String message;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getEncrptedId() {
        return encrptedId;
    }

    public void setEncrptedId(Integer encrptedId) {
        this.encrptedId = encrptedId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
