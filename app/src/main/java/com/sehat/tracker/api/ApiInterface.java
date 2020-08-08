package com.sehat.tracker.api;

import com.sehat.tracker.models.ResponseObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("search")
    Call<ResponseObject> getNews(
            @Query("q") String keyWord ,
            @Query("api-key") String lang,
            @Query("page-size") String sort,
            @Query("order-by") String apiKey,
            @Query("show-fields") String fields,
            @Query("shouldHideAdverts") String advert
    );

}
