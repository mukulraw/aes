package com.example.solomon.soloapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.example.solomon.soloapp.POJO.userBean;
import com.example.solomon.soloapp.interfaces.allAPIs;

import me.philio.pinentry.PinEntryView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SetPIN extends AppCompatActivity {


    PinEntryView pin;
    TextView set;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        bean b = (bean)getApplicationContext();

        id = b.id;

        pin = (PinEntryView)findViewById(R.id.pin);
        set = (TextView)findViewById(R.id.set);


        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setPIN(pin.getText().toString());

            }
        });




    }



    private void setPIN(String pin)
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://nationproducts.in/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        allAPIs cr = retrofit.create(allAPIs.class);


        Call<userBean> call = cr.setPIN(id , pin);

        call.enqueue(new Callback<userBean>() {
            @Override
            public void onResponse(Call<userBean> call, Response<userBean> response) {



                Intent intent = new Intent(getApplicationContext() , InsertPIN.class);
                startActivity(intent);
                finish();



            }

            @Override
            public void onFailure(Call<userBean> call, Throwable t) {

            }
        });


    }



}
