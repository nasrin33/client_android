package com.example.user.imageuploadretrofit3;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by user on 12/26/2018.
 */

public interface UploadImageInterface {

    @Multipart
    @POST("/Image_blog/default/api2/android_image")
    Call<UploadObject> uploadFile(@Part MultipartBody.Part file, @Part("name") RequestBody name);

}
