package com.brightfuture.eduquiz.helper;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
        @Headers("Content-Type: text/plain")
        @POST("create")
        Call<Map> create(@Body Map map);
      //  Call<Map> createRoom(@Body Map map);

}
