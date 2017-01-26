package com.project.client.repositories;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by root on 23/01/17.
 */

public interface ApiRepository {
    @POST("/oauth/token")
    @FormUrlEncoded
    Call<Map<String, String>> login(
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password);

    @POST("/oauth/token")
    @FormUrlEncoded
    Call<Map<String, String>> refreshToken(
            @Field("grant_type") String grantType,
            @Field("refresh_token") String refreshToken);

    @GET("/api/user")
    Call<Map<String, String>> getPrincipal();

}
