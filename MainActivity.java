package com.example.user.image_upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

     Button chooseBtn;
     Button uploadBTn;
     EditText enterName;
     ImageView imageView;
     final int IMG_REQUEST= 1;
     Bitmap bitmap;
    TextView encodedText;

    String Url = "http://192.168.99.1:8000/Image_blog/default/api2/android_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseBtn= (Button)findViewById(R.id.button);
        uploadBTn= (Button)findViewById(R.id.button2);
        enterName= (EditText)findViewById(R.id.editText);
        imageView= (ImageView)findViewById(R.id.imageView);
        encodedText= (TextView)findViewById(R.id.textView);

        chooseBtn.setOnClickListener(this);
        uploadBTn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button:
                selecImage();
                break;

            case R.id.button2:
                uploadImage();
                break;

        }
    }



    private void selecImage()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(galleryIntent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== IMG_REQUEST && resultCode ==RESULT_OK && data!= null){

            Uri path= data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                enterName.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest= new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();

            }
        }){
            protected Map<String, String> getParams(){
                Map <String, String> params= new HashMap<String, String>();
                String images = getStringImage(bitmap);

                String imageName=enterName.getText().toString();

                params.put("title", imageName);
                params.put("file", images);

                //mTextView.setText(msg);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    public String getStringImage(Bitmap bitmap){
        Log.i("MyHitesh",""+bitmap);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        //encodedText.setText(temp);


        return temp;
    }

}
