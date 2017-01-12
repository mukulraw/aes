package com.example.solomon.soloapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    String seedValue = "This Is MySecure";


    Button open;
    ImageView encImage , decImage;

    byte[] encodedBytes = null;
    byte[] decodedBytes = null;


    TextView tvencoded;
    TextView tvdecoded;

    Bitmap original , encrypted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScrollView scroll = (ScrollView)findViewById(R.id.scroll);

        scroll.setSmoothScrollingEnabled(true);


        tvencoded = (TextView)findViewById(R.id.enc);
        tvdecoded = (TextView)findViewById(R.id.dec);

        open = (Button)findViewById(R.id.open);
        encImage = (ImageView)findViewById(R.id.enc_image);
        decImage = (ImageView)findViewById(R.id.dec_image);



        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
// tells your intent to get the contents
// opens the URI for your image directory on your sdcard
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Uri selectedImageUri = data.getData();
            Log.d("URI VAL", "selectedImageUri = " + selectedImageUri.toString());


            try {
                original = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(String.valueOf(data.getData())));
            } catch (IOException e) {
                e.printStackTrace();
            }


            new doTask(original , encImage , decImage , tvencoded , tvdecoded).execute();





        }

    }



    public class doTask extends AsyncTask<Void , Void , Void>
    {

        Bitmap ori;
        ImageView encImage;
        ImageView decImage;
        TextView tvencoded;
        TextView tvdecoded;
        Bitmap bitmap;

        String e , d;


        public doTask(Bitmap ori , ImageView encImage , ImageView decImage , TextView tvencoded , TextView tvdecoded)
        {
            this.ori = ori;
            this.encImage = encImage;
            this.decImage = decImage;
            this.tvencoded = tvencoded;
            this.tvdecoded = tvdecoded;
        }


        @Override
        protected Void doInBackground(Void... params) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ori.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Log.d("original" , Arrays.toString(byteArray));

            //String theTestText = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

            SecretKeySpec sks = null;
            try {
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                sr.setSeed(seedValue.getBytes());
                KeyGenerator kg = KeyGenerator.getInstance("AES");
                kg.init(128, sr);
                sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
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

            encrypted = BitmapFactory.decodeByteArray(encodedBytes, 0,
                    encodedBytes.length);


            //setEncrypted(encrypted);


            e = Base64.encodeToString(encodedBytes, Base64.DEFAULT);





            // Decode the encoded data with AES

            try {
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.DECRYPT_MODE, sks);
                decodedBytes = c.doFinal(encodedBytes);
            } catch (Exception e) {
                Log.e("asdasasdasd" , "AES decryption error");
            }

            Log.d("decoded" , Arrays.toString(decodedBytes));

            bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0,
                    decodedBytes.length);



            return null;
        }




        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            encImage.setImageBitmap(encrypted);


            tvencoded.setText("[ENCODED]:\n" +
                    e);

            decImage.setImageBitmap(bitmap);


            tvdecoded.setText("[DECODED]:\n" + new String(decodedBytes));


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
