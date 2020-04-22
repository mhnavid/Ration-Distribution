package com.headblocks.rationdistribution.api;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("search-face")
    Call<ResponseBody> sendFaceImage(@Part MultipartBody.Part image);
}