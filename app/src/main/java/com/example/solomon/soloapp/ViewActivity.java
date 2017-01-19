package com.example.solomon.soloapp;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solomon.soloapp.POJO.AllFileDetail;
import com.example.solomon.soloapp.POJO.allBean;
import com.example.solomon.soloapp.interfaces.allAPIs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ViewActivity extends AppCompatActivity {

    RecyclerView grid;
    List<AllFileDetail> list;
    GridLayoutManager manager;
    GridAdapter adapter;
    String userId;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        dialog = new Dialog(ViewActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.progress_layout);



        grid = (RecyclerView)findViewById(R.id.grid);

        bean b = (bean)getApplicationContext();

        userId = b.id;

        list = new ArrayList<>();

        manager = new GridLayoutManager(this , 1);


        adapter = new GridAdapter(this , list);
        grid.setLayoutManager(manager);
        grid.setAdapter(adapter);



        dialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://nationproducts.in/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        allAPIs cr = retrofit.create(allAPIs.class);

        Call<allBean> call = cr.getAll(userId);

        call.enqueue(new Callback<allBean>() {
            @Override
            public void onResponse(Call<allBean> call, Response<allBean> response) {

                list = response.body().getAllFileDetail();
                adapter.setGridData(list);
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<allBean> call, Throwable t) {

                dialog.dismiss();

            }
        });



    }


    public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder>
    {

        List<AllFileDetail> list = new ArrayList<>();
        Context context;

        public GridAdapter(Context context , List<AllFileDetail> list)
        {
            this.list = list;
            this.context = context;
        }

        public void setGridData(List<AllFileDetail> list)
        {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_model , parent , false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            final AllFileDetail item = list.get(position);

            holder.name.setText(item.getFileName());


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.show();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://nationproducts.in/")
                            .build();

                    allAPIs cr = retrofit.create(allAPIs.class);

                    cr.getFile(item.getEncriptfile()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                        DownloadFileAsyncTask downloadFileAsyncTask = new DownloadFileAsyncTask(item.getEncriptfileId() , item.getFileName());
                        downloadFileAsyncTask.execute(response.body().byteStream());


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });

                }
            });





        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            TextView name;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.name);

            }
        }

    }


    private class DownloadFileAsyncTask extends AsyncTask<InputStream, Void, Boolean> {

        String TAG = "asdasdasd";



        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+ "/SoloApp/");



        final String filename;

        String key;

        String path = Environment.getExternalStorageDirectory().toString();

        byte[] decodedBytes = null;

        File file;

        public DownloadFileAsyncTask(String key , String name)
        {
            this.key = key;
            this.filename = name;
        }


        @Override
        protected Boolean doInBackground(InputStream... params) {

            if (!(dir.exists() && dir.isDirectory())) {
                dir.mkdirs();
            }

            InputStream inputStream = params[0];



            try {
                file = new File(dir , filename);

                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }


            OutputStream output = null;
            try {
                output = new FileOutputStream(file);

                byte[] buffer = new byte[1024]; // or other buffer size
                int read;

                Log.d(TAG, "Attempting to write to: " + dir + "/" + filename);
                while ((read = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                    Log.v(TAG, "Writing to buffer to output stream.");
                }
                Log.d(TAG, "Flushing output stream.");
                output.flush();
                Log.d(TAG, "Output flushed.");
            } catch (IOException e) {
                Log.e(TAG, "IO Exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (output != null) {
                        output.close();
                        Log.d("Asdasdasd", "Output stream closed sucessfully.");
                    }
                    else{
                        Log.d(TAG, "Output stream is null");
                    }
                } catch (IOException e){
                    Log.e("Asdasdasd", "Couldn't close output stream: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }






                byte[] byteArray = new byte[0];
                try {
                    byteArray = Util.readBytesFromFile(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    Cipher c = Cipher.getInstance("AES");

                    //Log.d("asdasdKEY2" , Arrays.toString(new SecretKeySpec(Base64.decode(key , Base64.NO_PADDING), "AES").getEncoded()));

                    Log.d("asdasdKEY2" , Base64.encodeToString(new SecretKeySpec(Base64.decode(key , Base64.NO_PADDING), "AES").getEncoded() , Base64.DEFAULT));

                    c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decode(key , Base64.NO_PADDING) , "AES"));
                    decodedBytes = c.doFinal(byteArray);
                } catch (Exception e) {
                    Log.e("asdasasdasd" , "AES decryption error");
                }






            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);


            try {
                Util.writeBytesToFile(file , decodedBytes);
            } catch (IOException e1) {
                e1.printStackTrace();
            }


            Toast.makeText(getApplicationContext() , "Download Success" , Toast.LENGTH_SHORT).show();

            dialog.dismiss();

            Log.d("asdasdasd", "Download success: " + result);
            // TODO: show a snackbar or a toast
        }
    }


}
