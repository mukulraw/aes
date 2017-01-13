package com.example.solomon.soloapp.interfaces;


import com.example.solomon.soloapp.POJO.userBean;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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


}
