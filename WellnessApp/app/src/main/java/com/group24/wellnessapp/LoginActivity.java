package com.group24.wellnessapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    static String userID = null;
    static String token = null;

    // AsyncTask for JSON requests
    class loginAsyncTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground (String...userID) {
            final EditText usernameEditText = findViewById(R.id.usernameEditText);
            final EditText passwordEditText = findViewById(R.id.passwordEditText);

            // Fill text fields if previously filled
            usernameEditText.setText(getIntent().getStringExtra("username"));
            passwordEditText.setText(getIntent().getStringExtra("password"));

            // Log in button to go to LogActivity screen
            Button logInBtn = findViewById(R.id.logInBtn);
            logInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView loginErrorTextView = findViewById(R.id.loginErrorTextView);

                    // Test login credentials
                    try {
                        if (userLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString()) != null) {
                            loginErrorTextView.setVisibility(View.GONE);
                            try {
                                setUserID(userLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(startIntent);
                        } else {
                            loginErrorTextView.setVisibility(View.VISIBLE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }

        protected void onPostExecute (Void v) {
            // Remove back button from toolbar
            if (getSupportActionBar() != null) {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(false);
            }

            // Go to register screen
            TextView registerTextView = findViewById(R.id.registerTextView);
            registerTextView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    // Getters and setters for userID and token
    public static String getUserID() {
        return userID;
    }
    public static void setUserID(String newUserID) {
        userID = newUserID;
    }
    public static String getToken() {
        return token;
    }
    public static void setToken(String newToken) {
        token = newToken;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Start AsyncTask
        new loginAsyncTask().execute("text");
    }

    public static String userLogin(String username, String password) throws IOException {
        // Connect to server
        URL obj = new URL("http://3.92.227.189:80/api/users/login");
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Content-Type", "application/json");

        // Create JSON object with username and password
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("username", username);
            jObj.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Send data
        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(jObj.toString().getBytes());
        os.flush();
        os.close();

        // Get response
        int responseCode = postConnection.getResponseCode();
        System.out.println("POST Response Code :  " + responseCode);
        System.out.println("POST Response Message : " + postConnection.getResponseMessage());
        if (responseCode == HttpURLConnection.HTTP_CREATED) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    postConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();

            // Print result
            System.out.println(response.toString());
            JSONObject jObjResponse = null;
            try {
                jObjResponse = new JSONObject(response.toString());
                setToken(jObjResponse.getString("token"));
                return jObjResponse.getString("_id");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            // Error message
            System.out.println("POST NOT WORKED");
            return null;
        }
    }
}