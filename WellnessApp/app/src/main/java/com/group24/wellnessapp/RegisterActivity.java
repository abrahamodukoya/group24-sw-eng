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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    class registerAsyncTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground (String...userID) {
            final EditText usernameEditText = findViewById(R.id.usernameEditText);
            final EditText passwordEditText = findViewById(R.id.passwordEditText);

            // Log in button to go to LogActivity screen
            Button registerBtn = findViewById(R.id.registerBtn);
            registerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView registerErrorTextView = findViewById(R.id.registerErrorTextView);
                    try {
                        if (MyPOSTRequest(usernameEditText.getText().toString(), passwordEditText.getText().toString()) == true) {
                            registerErrorTextView.setVisibility(View.GONE);
                            Intent startIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            startIntent.putExtra("username", usernameEditText.getText().toString());
                            startIntent.putExtra("password", passwordEditText.getText().toString());
                            startActivity(startIntent);
                        } else {
                            registerErrorTextView.setVisibility(View.VISIBLE);
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        new registerAsyncTask().execute("text");
    }

    public static boolean MyPOSTRequest(String username, String password) throws IOException {
        URL obj = new URL("http://3.92.227.189:80/api/users/register");
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Content-Type", "application/json");

        JSONObject jObj = new JSONObject();
        try {
            jObj.put("username", username);
            jObj.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("jObj", jObj.toString());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(jObj.toString().getBytes());
        os.flush();
        os.close();

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
            // print result
            System.out.println(response.toString());
            JSONObject jObjResponse = null;
            try {
                jObjResponse = new JSONObject(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            System.out.println("POST NOT WORKED");
            return false;
        }
    }
}