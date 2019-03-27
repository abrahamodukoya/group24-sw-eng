package com.group24.wellnessapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {
    private Context context;
    String userID = "5c97c67625931e464ff8293f";

    class addAsyncTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground (final String...userID) {
            try  {
                // Activity category drop down menu
                Spinner categoryDropDown = findViewById(R.id.categorySelectionSpinner);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                        R.array.activityCategoryOptions, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                categoryDropDown.setAdapter(adapter);

                // Log new activity and send data to MainActivity screen
                Button addActivityBtn = findViewById(R.id.addActivityBtn);
                addActivityBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Grab different activity data entries
                        EditText activityLabelText = findViewById(R.id.activityLabelText);
                        EditText activityTimeText = findViewById(R.id.activityTimeText);
                        Spinner activityCategorySpinner = findViewById(R.id.categorySelectionSpinner);
                        String userID = "5c97c67625931e464ff8293f";
                        Intent sendActivityData = new Intent(getApplicationContext(), LogActivity.class);

                        try {
                            MyPATCHRequest(userID, activityCategorySpinner.getSelectedItem().toString(), activityLabelText.getText().toString(), activityTimeText.getText().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        sendActivityData.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(sendActivityData);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        context = this;

        // Remove back button from toolbar
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        new addAsyncTask().execute(userID);
        /*
        // Activity category drop down menu
        Spinner categoryDropDown = findViewById(R.id.categorySelectionSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activityCategoryOptions, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categoryDropDown.setAdapter(adapter);

        // Log new activity and send data to MainActivity screen
        Button addActivityBtn = findViewById(R.id.addActivityBtn);
        addActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Grab different activity data entries
                EditText activityLabelText = findViewById(R.id.activityLabelText);
                EditText activityTimeText = findViewById(R.id.activityTimeText);
                Spinner activityCategorySpinner = findViewById(R.id.categorySelectionSpinner);

                // Send activity data to MainActivity screen
                Intent sendActivityData = new Intent(getApplicationContext(), LogActivity.class);
                sendActivityData.putExtra("ActivityLabel", activityLabelText.getText().toString());
                sendActivityData.putExtra("ActivityCategory", activityCategorySpinner.getSelectedItem().toString());
                sendActivityData.putExtra("ActivityTime", activityTimeText.getText().toString());

                sendActivityData.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(sendActivityData);
            }
        });
        */
    }

    public static void MyPATCHRequest(String userID, String type, String label, String duration) throws IOException {
        /*
        final String PATCH_PARAMS = "{\n" + "\"userId\": 101,\r\n" +
                "    \"id\": 101,\r\n" +
                "    \"title\": \"Test Title\",\r\n" +
                "    \"body\": \"Test Body\"" + "\n}";
        System.out.println(PATCH_PARAMS);
        */
        URL obj = new URL("http://3.92.227.189:80/api/getDate/" + userID);
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod("PATCH");
        postConnection.setRequestProperty("Content-Type", "application/json");

        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(calendar.getTime());

        JSONObject jObj = new JSONObject();
        try {
            jObj.put("duration", duration);
            jObj.put("label", label);
            jObj.put("type", type);
            jObj.put("date", currentDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(jObj.toString().getBytes());
        os.flush();
        os.close();

        int responseCode = postConnection.getResponseCode();
        System.out.println("POST Response Code :  " + responseCode);
        System.out.println("POST Response Message : " + postConnection.getResponseMessage());

        /*
        if (responseCode == HttpURLConnection.HTTP_CREATED) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST NOT WORKED");
        }
        */
    }
}