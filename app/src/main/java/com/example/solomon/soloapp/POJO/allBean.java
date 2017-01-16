package com.example.solomon.soloapp.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class allBean {

    @SerializedName("all_file_detail")
    @Expose
    private List<AllFileDetail> allFileDetail = null;

    public List<AllFileDetail> getAllFileDetail() {
        return allFileDetail;
    }

    public void setAllFileDetail(List<AllFileDetail> allFileDetail) {
        this.allFileDetail = allFileDetail;
    }

}
