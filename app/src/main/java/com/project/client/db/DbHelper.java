package com.project.client.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by root on 23/01/17.
 */

public class DbHelper {
    private Context context;

    public DbHelper(Context context) {
        this.context = context;
    }

    public void saveOAuthToken(String token) {
        // Access Shared Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("access_token", token);
        editor.apply();
    }

    public String getOAuthToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("access_token", null);
    }

    public void saveRefreshToken(String token) {
        // Access Shared Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("refresh_token", token);
        editor.apply();
    }

    public String getRefreshToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("refresh_token", null);
    }
}
