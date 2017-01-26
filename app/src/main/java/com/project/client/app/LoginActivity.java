package com.project.client.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.client.services.ApiServices;

public class LoginActivity extends AppCompatActivity {
    private ApiServices services;
    // UI references.
    private EditText mUserIdView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserIdView = (EditText) findViewById(R.id.user_id);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        services = new ApiServices(getApplicationContext());
    }

    private void attemptLogin() {
        final String userId = mUserIdView.getText().toString();
        final String password = mPasswordView.getText().toString();


        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return services.login(userId, password);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    Toast.makeText(LoginActivity.this, "User is authenticated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(userId, password);

    }


}

