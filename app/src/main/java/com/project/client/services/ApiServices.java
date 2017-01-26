package com.project.client.services;

import android.content.Context;

import com.project.client.configuration.Config;
import com.project.client.db.DbHelper;
import com.project.client.repositories.ApiRepository;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by root on 23/01/17.
 */

public class ApiServices {

    private Context context;


    public ApiServices(Context context) {
        this.context = context;

    }

    public boolean login(String userName, String password) {
        ApiRepository repository = ServiceGenerator.createService(ApiRepository.class);
        Call<Map<String, String>> call = repository.login(Config.GRANT_TYPE_PASSWORD, userName, password);
        try {
            retrofit2.Response<Map<String, String>> response = call.execute();
            if (response.code() == 200) {
                Map<String, String> result = response.body();

                DbHelper db = new DbHelper(context);
                db.saveOAuthToken(result.get("access_token"));
                db.saveRefreshToken(result.get("refresh_token"));
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public String getPrincipal() {
        DbHelper db = new DbHelper(context);
        ApiRepository repository = ServiceGenerator.createService(ApiRepository.class, db.getOAuthToken(), context);
        Map<String, String> result = null;
        try {
            result = repository.getPrincipal().execute().body();
            return result.get("user");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
