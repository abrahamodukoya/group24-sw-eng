package com.group24.wellnessapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Remove back button from toolbar
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        // Log in button to go to MainActivity screen
        Button logInBtn = findViewById(R.id.logInBtn);
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
            }
        });

        Button requestBtn = findViewById(R.id.requestBtn);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            MyGETRequest();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        });
    }

    public static void MyGETRequest() throws IOException {
        //URL urlForGetRequest = new URL("http://3.92.227.189:80/api/users");
        URL urlForGetRequest = new URL("http://3.92.227.189:80/api/getDate/5c97c67625931e464ff8293f/2018-03-24");
        String readLine = null;
        HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            } in.close();

            JSONObject jObj = null;
            JSONArray jArr = null;

            try {
                jObj = new JSONObject(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                 jArr = jObj.getJSONArray("activity");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < jArr.length(); i++) {
                try {
                    Log.d("JSON String Result " + i, jArr.getJSONObject(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("1", "GET NOT WORKED");
        }
    }
}