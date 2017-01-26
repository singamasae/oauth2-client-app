package com.project.client.services;

import android.content.Context;
import android.util.Base64;

import com.project.client.configuration.Config;
import com.project.client.db.DbHelper;
import com.project.client.repositories.ApiRepository;

import java.io.IOException;
import java.util.Map;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by root on 24/01/17.
 */

public class ServiceGenerator {


    private static Retrofit retrofit;

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Config.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {


        httpClient = new OkHttpClient.Builder();
        builder = new Retrofit.Builder()
                .baseUrl(Config.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        String credential = Config.CLIENT_ID + ":" + Config.CLIENT_SECRET;
        final String basicAuthHeader = "Basic " + Base64.encodeToString(credential.getBytes(), Base64.NO_WRAP);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Authorization", basicAuthHeader)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken, final Context c) {
        httpClient = new OkHttpClient.Builder();
        builder = new Retrofit.Builder()
                .baseUrl(Config.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        if (authToken != null) {

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + authToken)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            httpClient.authenticator(new Authenticator() {
                @Override
                public Request authenticate(Route route, Response response) throws IOException {
                    if (responseCount(response) >= 2) {
                        // If both the original call and the call with refreshed token failed,
                        // it will probably keep failing, so don't try again.
                        return null;
                    }

                    DbHelper db = new DbHelper(c);
                    ApiRepository repository = createService(ApiRepository.class);
                    Call<Map<String, String>> call = repository.refreshToken(Config.GRANT_TYPE_REFRESH_TOKEN, db.getRefreshToken());
                    retrofit2.Response<Map<String, String>> tokenResponse = call.execute();

                    if (tokenResponse.code() == 200) {
                        Map<String, String> result = tokenResponse.body();

                        String new_access_token = result.get("access_token");
                        String new_refresh_token = result.get("refresh_token");

                        db.saveOAuthToken(new_access_token);
                        db.saveRefreshToken(new_refresh_token);

                        return response.request().newBuilder()
                                .header("Authorization", "Bearer " + new_access_token)
                                .build();
                    }

                    return null;
                }
            });
        }

        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
        }

        builder.client(httpClient.build());
        retrofit = builder.build();

        return retrofit.create(serviceClass);
    }

    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
