package com.example.solomon.soloapp.interfaces;


import com.example.solomon.soloapp.POJO.allBean;
import com.example.solomon.soloapp.POJO.uploadBean;
import com.example.solomon.soloapp.POJO.userBean;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface allAPIs {


    @Multipart
    @POST("solo/register.php")
    Call<userBean> register(@Part("uniqid") String uniqId, @Part("name") String name);



    @Multipart
    @POST("solo/login.php ")
    Call<userBean> login(@Part("uniqid") String uniqId, @Part("name") String name);

    @Multipart
    @POST("solo/userpin_update.php ")
    Call<userBean> setPIN(@Part("userid") String id, @Part("userpin") String pin);

    @Multipart
    @POST("solo/userpin_check.php ")
    Call<userBean> checkPIN(@Part("userid") String id, @Part("userpin") String pin);


    @Multipart
    @POST("solo/insertfile.php ")
    Call<uploadBean> upload(@Part("user_id") String id, @Part("encrepted_key") String key , @Part("file_name") String fileName , @Part MultipartBody.Part file) ;


    @Multipart
    @POST("solo/all_file.php")
    Call<allBean> getAll(@Part("user_id") String id);

    @GET
    @Streaming
    Call<ResponseBody> getFile(@Url String url);



}
