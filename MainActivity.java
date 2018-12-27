package com.example.user.imageuploadretrofit3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    Button chooseImage;
    Button UploadImage;
    ImageView imageView;
    Bitmap bitmap;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://192.168.99.1:8000/";
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseImage= (Button)findViewById(R.id.chooseImage);
        UploadImage= (Button)findViewById(R.id.uploadImage);
        imageView= (ImageView)findViewById(R.id.imageView);


        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
            }
        });

        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filePath = getRealPathFromURIPath(uri, MainActivity.this);
                File file = new File(filePath);
                Log.d(TAG, "Filename " + file.getName());
                //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_PATH)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
                Call<UploadObject> fileUpload = uploadImage.uploadFile(fileToUpload, filename);

                fileUpload.enqueue(new Callback<UploadObject>() {
                    @Override
                    public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {
                        Toast.makeText(MainActivity.this, "Response " + response.raw().message(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainActivity.this, "Success " + response.body().getSuccess(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<UploadObject> call, Throwable t) {
                        Log.d(TAG, "Error " + t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                try {
                    bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                    //enterName.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            else {
                EasyPermissions.requestPermissions(this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);

                Toast.makeText(this, "Unable to select image", Toast.LENGTH_SHORT).show();
                return;
            }

            }
        }


    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }



    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        if(uri != null){
            String filePath = getRealPathFromURIPath(uri, MainActivity.this);
            File file = new File(filePath);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_PATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
            Call<UploadObject> fileUpload = uploadImage.uploadFile(fileToUpload, filename);
            fileUpload.enqueue(new Callback<UploadObject>() {
                @Override
                public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {
                    Toast.makeText(MainActivity.this, "Success " + response.message(), Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, "Success " + response.body().toString(), Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Call<UploadObject> call, Throwable t) {
                    Log.d(TAG, "Error " + t.getMessage());
                }
            });
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        Log.d(TAG, "Permission has been denied");

    }
}
