package com.group24.wellnessapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Go to AddActivity screen
        Button goToAddActivityBtn = findViewById(R.id.goToAddActivityBtn);
        goToAddActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(startIntent);
            }
        });

        // Get logged activity data from AddActivity screen
        String[] activityObject = new String[4];

        if (getIntent().hasExtra("ActivityLabel") && getIntent().hasExtra("ActivityTime") && getIntent().hasExtra("ActivityCategory")) {
            // Store activity data into array
            activityObject[0] = getIntent().getExtras().getString("ActivityLabel");
            activityObject[1] = getIntent().getExtras().getString("ActivityTime");
            activityObject[2] = getIntent().getExtras().getString("ActivityCategory");

            // Get current date and store it in activity array
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            activityObject[3] = dateFormat.format(calendar.getTime());

            /*
            Log.d("LABEL", activityObject[0]);
            Log.d("TIME", activityObject[1]);
            Log.d("CATEGORY", activityObject[2]);
            Log.d("DATE", activityObject[3]);
            */
        }
    }
}