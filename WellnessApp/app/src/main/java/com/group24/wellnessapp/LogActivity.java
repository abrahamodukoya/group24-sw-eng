package com.group24.wellnessapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogActivity extends AppCompatActivity {
    private Context context;
    String userID = "5c97c67625931e464ff8293f";
    JSONArray jArr = null;

    class loginAsyncTask extends AsyncTask<String, Void, Void> {
        // Get activity array
        protected Void doInBackground (String...userID) {
            try  {
                jArr = MyGETRequest(userID[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute (Void v) {
            // Add activities to log screen
            for (int i = 0; i < jArr.length(); i++) {
                // Adding activities
                LinearLayout parentLinearLayout = findViewById(R.id.parentLinearLayout);

                LinearLayout newLayout = new LinearLayout(context);
                //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(900, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = 25;
                newLayout.setLayoutParams(layoutParams);
                newLayout.setBackgroundResource(R.drawable.customborder);
                newLayout.setOrientation(LinearLayout.VERTICAL);

                TextView activityCategoryTextView = new TextView(context);
                TextView activityLabelTextView = new TextView(context);
                TextView activityTimeTextView = new TextView(context);
                TextView activityDateTextView = new TextView(context);

                try {
                    activityCategoryTextView.setText(jArr.getJSONObject(i).getString("type"));
                    activityLabelTextView.setText(jArr.getJSONObject(i).getString("label"));
                    activityTimeTextView.setText(jArr.getJSONObject(i).getString("duration"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                activityDateTextView.setText("2018-03-24");

                newLayout.addView(activityCategoryTextView);
                newLayout.addView(activityLabelTextView);
                newLayout.addView(activityTimeTextView);
                newLayout.addView(activityDateTextView);

                parentLinearLayout.addView(newLayout);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_log);

        // Remove back button from toolbar
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        // Go to AddActivity screen
        Button addActivityFromLog = findViewById(R.id.addActivityFromLogBtn);
        addActivityFromLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(startIntent);
            }
        });

        // Go to Analytics screen
        Button goToAnalytics = findViewById(R.id.goToAnalyticsBtn);
        goToAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), AnalyticsActivity.class);
                startActivity(startIntent);
            }
        });

        new loginAsyncTask().execute(userID);
    }

    public static JSONArray MyGETRequest(String userID) throws IOException {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //String currentDate = dateFormat.format(calendar.getTime());
        String currentDate = "2018-03-24";

        URL urlForGetRequest = new URL("http://3.92.227.189:80/api/getDate/" + userID + "/" + currentDate);
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

            return jArr;
        } else {
            Log.d("1", "GET NOT WORKED");
            return null;
        }
    }
}