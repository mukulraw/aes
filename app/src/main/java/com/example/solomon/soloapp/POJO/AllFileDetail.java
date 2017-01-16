package com.example.solomon.soloapp.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AllFileDetail {


    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("encriptfile_id")
    @Expose
    private String encriptfileId;
    @SerializedName("encriptfile")
    @Expose
    private String encriptfile;
    @SerializedName("file_name")
    @Expose
    private String fileName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEncriptfileId() {
        return encriptfileId;
    }

    public void setEncriptfileId(String encriptfileId) {
        this.encriptfileId = encriptfileId;
    }

    public String getEncriptfile() {
        return encriptfile;
    }

    public void setEncriptfile(String encriptfile) {
        this.encriptfile = encriptfile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
