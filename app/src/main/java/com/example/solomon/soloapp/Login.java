package com.example.solomon.soloapp;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solomon.soloapp.POJO.userBean;
import com.example.solomon.soloapp.interfaces.allAPIs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    private TextView google_signin;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int RC_SIGN_IN = 9001;

    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if(!hasPermissions(this , PERMISSIONS))
        {
            ActivityCompat.requestPermissions(this , PERMISSIONS , REQUEST_CODE_ASK_PERMISSIONS);
        }




        google_signin = (TextView) findViewById(R.id.sign_in_button);

        dialog = new Dialog(Login.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.progress_layout);



        if(checkPlayServices())
        {

            buildGoogleApiClient();

            google_signin.setOnClickListener(this);

        }


    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS)
        {
            if (ActivityCompat.checkSelfPermission(getApplicationContext() , Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {



            }
            else
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    Toast.makeText(getApplicationContext() , "Permissions are required for this app" , Toast.LENGTH_SHORT).show();
                    finish();

                }
                //permission is denied (and never ask again is  checked)
                //shouldShowRequestPermissionRationale will return false
                else {
                    Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                            .show();
                    finish();
                    //                            //proceed with logic by disabling the related features or quit the app.
                }
            }

        }


    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }





    private boolean checkPlayServices() {

        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);

        if (GooglePlayServicesUtil.isUserRecoverableError(checkGooglePlayServices)) {
            showGooglePlayServicesAvailabilityErrorDialog(checkGooglePlayServices);
            return false;
        }


        return true;

    }



    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,Login.this,
                        PLAY_SERVICES_RESOLUTION_REQUEST);
                if (dialog == null) {
                    //Log.e("DEBUG_TAG",
                    //"couldn't get GooglePlayServicesUtil.getErrorDialog");
                    Toast.makeText(getBaseContext(),
                            "incompatible version of Google Play Services",
                            Toast.LENGTH_LONG).show();



                    dialog.show();
                }
                //this was wrong here -->dialog.show();
            }
        });
    }

    private synchronized void buildGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
        .enableAutoManage(this , this)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build();
    }

    private void signIn() {
        dialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sign_in_button:
            signIn();
            break;

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
}
    }




    private void handleSignInResult(GoogleSignInResult result) {



        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.

            GoogleSignInAccount acct = result.getSignInAccount();



            register(String.valueOf(acct.getId()) , acct.getDisplayName() , acct.getEmail());





        } else {

            // Signed out, show unauthenticated UI.

            Log.d("asdasdasd" , "Signed Out");

        }

    }



    private void register(final String id , final String name , final String email)
    {

        Log.d("asdasdasdasdasd" , "register");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://nationproducts.in/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        allAPIs cr = retrofit.create(allAPIs.class);


        Call<userBean> call = cr.register(id , name , email);


        call.enqueue(new Callback<userBean>() {
            @Override
            public void onResponse(Call<userBean> call, Response<userBean> response) {


                bean b = (bean)getApplicationContext();

                b.email = id;
                b.name = name;

                login(id , name);



            }

            @Override
            public void onFailure(Call<userBean> call, Throwable t) {

                dialog.dismiss();

            }
        });





    }



    private  void login(String id , String name)
    {


        Log.d("asdasdasdasdasd" , "login");



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://nationproducts.in/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        allAPIs cr = retrofit.create(allAPIs.class);

        Call<userBean> call = cr.login(id , name);


        call.enqueue(new Callback<userBean>() {
            @Override
            public void onResponse(Call<userBean> call, Response<userBean> response) {


                bean b = (bean)getApplicationContext();


                    if (response.body().getUserpinAvailable()) {


                        b.id = response.body().getUserId();

                        Intent intent = new Intent(getApplicationContext() , InsertPIN.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();

                        Log.d("ASDASD", "PIN available");
                    } else {

                        b.id = response.body().getUserId();

                        Intent intent = new Intent(getApplicationContext() , SetPIN.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();

                        Log.d("ASDASD", "PIN unavailable");
                    }





            }

            @Override
            public void onFailure(Call<userBean> call, Throwable t) {

                dialog.dismiss();

            }
        });


    }


}
