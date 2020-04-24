package com.headblocks.rationdistribution.api;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("search-face")
    Call<ResponseBody> sendFaceImage(@Part MultipartBody.Part image,
                                     @Part("name") RequestBody name);

    @FormUrlEncoded
    @POST("api/v1/date-update")
    Call<ResponseBody> sendStatus(@Field("id") String id,
                                  @Field("status") String status);
}