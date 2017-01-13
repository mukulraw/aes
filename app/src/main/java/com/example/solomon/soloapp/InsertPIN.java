package com.example.solomon.soloapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solomon.soloapp.POJO.userBean;
import com.example.solomon.soloapp.interfaces.allAPIs;

import java.util.Objects;

import me.philio.pinentry.PinEntryView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class InsertPIN extends AppCompatActivity {

    PinEntryView pin;
    TextView set;
    String id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_pin);

        pin = (PinEntryView)findViewById(R.id.pin);
        set = (TextView)findViewById(R.id.set);

        bean b = (bean)getApplicationContext();

        id = b.id;

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                checkPIN(pin.getText().toString());


            }
        });



    }


    private  void checkPIN(String pin)
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://nationproducts.in/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        allAPIs cr = retrofit.create(allAPIs.class);


        Call<userBean> call = cr.checkPIN(id , pin);


        call.enqueue(new Callback<userBean>() {
            @Override
            public void onResponse(Call<userBean> call, Response<userBean> response) {


                if (Objects.equals(response.body().getMessage(), "Userpin Valid!!.."))
                {

                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                    startActivity(intent);
                    finish();

                }
                else
                {

                    Toast.makeText(getApplicationContext() , "Invalid PIN" , Toast.LENGTH_SHORT).show();

                }



            }

            @Override
            public void onFailure(Call<userBean> call, Throwable t) {

            }
        });

    }


}
