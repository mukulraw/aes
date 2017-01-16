package com.example.solomon.soloapp;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.solomon.soloapp.POJO.uploadBean;
import com.example.solomon.soloapp.interfaces.allAPIs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    String seedValue = "This Is MySecure";

    Button download;

    Button open;
    ImageView encImage , decImage;

    byte[] encodedBytes = null;
    byte[] decodedBytes = null;

    String path;
    ProgressBar progress;

    String userId;

    String filename;
    String key;

    TextView tvencoded;
    TextView tvdecoded;

    Bitmap original , encrypted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progress = (ProgressBar)findViewById(R.id.progress);

        bean b = (bean)getApplicationContext();

        userId = b.id;

        download = (Button)findViewById(R.id.view);


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext() , ViewActivity.class);
                startActivity(intent);

            }
        });



        open = (Button)findViewById(R.id.open);




        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress.setVisibility(View.VISIBLE);

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
// tells your intent to get the contents
// opens the URI for your image directory on your sdcard
                intent.setType("*/*");
                startActivityForResult(intent, 1);

            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Uri selectedImageUri = data.getData();



            path = getPath(getApplicationContext() , selectedImageUri);

            Cursor returnCursor =
                    getContentResolver().query(selectedImageUri, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);


            //filename = returnCursor.getString(nameIndex);

            new doTask().execute();





        }

    }


    private static String getPath(final Context context, final Uri uri)
    {
        final boolean isKitKatOrAbove = Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKatOrAbove && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }


    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public class doTask extends AsyncTask<Void , Void , Void>
    {

        File f = null;

        String e , d;


        public doTask()
        {

        }


        @Override
        protected Void doInBackground(Void... params) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();


            if (path!=null)
            {
                f = new File(path);
            }


            byte[] byteArray = new byte[0];
            try {
                byteArray = Util.readBytesFromFile(f);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Log.d("original" , Arrays.toString(byteArray));

            //String theTestText = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

            SecretKeySpec sks = null;
            try {
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                sr.setSeed(seedValue.getBytes("UTF-8"));
                KeyGenerator kg = KeyGenerator.getInstance("AES");
                kg.init(128, sr);

                sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");

                //key = Arrays.toString(sks.getEncoded());

                key = Base64.encodeToString(sks.getEncoded() , Base64.NO_PADDING);


                Log.d("asdasdasKEY" , key);

            } catch (Exception e) {
                Log.e("asdasdasd", "AES secret key spec error");
            }

            // Encode the original data with AES

            try {
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.ENCRYPT_MODE, sks);
                encodedBytes = c.doFinal(byteArray);



            } catch (Exception e) {
                Log.e("asdasdasd", "AES encryption error");
            }

            Log.d("encrypted" , Arrays.toString(encodedBytes));



            //setEncrypted(encrypted);


            e = Base64.encodeToString(encodedBytes, Base64.DEFAULT);





            // Decode the encoded data with AES

            try {
                Cipher c = Cipher.getInstance("AES");

                //Log.d("asdasdKEY2" , Arrays.toString(new SecretKeySpec(Base64.decode(key , Base64.NO_PADDING), "AES").getEncoded()));

                Log.d("asdasdKEY2" , Base64.encodeToString(new SecretKeySpec(Base64.decode(key , Base64.NO_PADDING), "AES").getEncoded() , Base64.DEFAULT));

                c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decode(key , Base64.NO_PADDING) , "AES"));
                decodedBytes = c.doFinal(encodedBytes);
            } catch (Exception e) {
                Log.e("asdasasdasd" , "AES decryption error");
            }

            Log.d("decoded" , Arrays.toString(decodedBytes));




            return null;
        }




        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);



            try {
                Util.writeBytesToFile(f , encodedBytes);
            } catch (IOException e1) {
                e1.printStackTrace();
            }


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://nationproducts.in/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            allAPIs cr = retrofit.create(allAPIs.class);

            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);

            filename = f.getName();

            MultipartBody.Part body = MultipartBody.Part.createFormData("encreptedfile", f.getName(), reqFile);

            Call<uploadBean> call = cr.upload(userId , key , filename , body);

            call.enqueue(new Callback<uploadBean>() {
                @Override
                public void onResponse(Call<uploadBean> call, Response<uploadBean> response) {
                    progress.setVisibility(View.GONE);


                }

                @Override
                public void onFailure(Call<uploadBean> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                }
            });


        }



    }



    private void setEncrypted(Bitmap encrypted)
    {
        encImage.setImageBitmap(encrypted);


        tvencoded.setText("[ENCODED]:\n" +
                Base64.encodeToString(encodedBytes, Base64.DEFAULT) + "\n");

    }


    private void setDecrypted(Bitmap decrypted)
    {


    }



}
