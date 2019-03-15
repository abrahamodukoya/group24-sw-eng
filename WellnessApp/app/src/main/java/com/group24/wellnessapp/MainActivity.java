package com.group24.wellnessapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    // Boolean that monitors whether user has logged any activity yet
    boolean hasLoggedActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting layout based on whether user has logged activity or not
        // Hasn't logged elements
        Button goToAddActivityBtn = findViewById(R.id.goToAddActivityBtn);
        TextView addActivityPromptTextView = findViewById(R.id.addActivityPromptTextView);
        // Has logged elements
        ScrollView loggedActivityScrollView = findViewById(R.id.loggedActivityScrollView);
        LinearLayout loggedActivityLinearLayout = findViewById(R.id.loggedActivityLinearLayout);
        Button addActivityFromLogBtn = findViewById(R.id.addActivityFromLogBtn);
        Button goToAnalyticsBtn = findViewById(R.id.goToAnalyticsBtn);

        TextView activityCategoryTextView = findViewById(R.id.activityCategoryTextView);
        TextView activityLabelTextView = findViewById(R.id.activityLabelTextView);
        TextView activityTimeTextView = findViewById(R.id.activityTimeTextView);
        TextView activityDateTextView = findViewById(R.id.activityDateTextView);

        if (hasLoggedActivity == false) {
            // Hasn't logged elements
            goToAddActivityBtn.setVisibility(View.VISIBLE);
            addActivityPromptTextView.setVisibility(View.VISIBLE);
            // Has logged elements
            loggedActivityScrollView.setVisibility(View.GONE);
            loggedActivityLinearLayout.setVisibility(View.GONE);
            addActivityFromLogBtn.setVisibility(View.GONE);
            goToAnalyticsBtn.setVisibility(View.GONE);
        } else {
            // Hasn't logged elements
            goToAddActivityBtn.setVisibility(View.GONE);
            addActivityPromptTextView.setVisibility(View.GONE);
            // Has logged elements
            loggedActivityScrollView.setVisibility(View.VISIBLE);
            loggedActivityLinearLayout.setVisibility(View.VISIBLE);
            addActivityFromLogBtn.setVisibility(View.VISIBLE);
            goToAnalyticsBtn.setVisibility(View.VISIBLE);
        }

        // Go to AddActivity screen
        goToAddActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), AddActivity.class);
                startIntent.putExtra("HasLoggedActivityValue", hasLoggedActivity);
                startActivity(startIntent);
            }
        });

        // Go to Analytics screen
        Button goToAnalytics = findViewById(R.id.goToAnalyticsBtn);
        goToAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), AnalyticsActivity.class);
                //startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startIntent);
            }
        });

        // Receive activity data from AddActivity
        if (getIntent().hasExtra("ActivityLabel") && getIntent().hasExtra("ActivityTime") && getIntent().hasExtra("ActivityCategory")) {
            // Get current date
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            activityCategoryTextView.setText(">" + getIntent().getExtras().getString("ActivityCategory"));
            activityLabelTextView.setText(getIntent().getExtras().getString("ActivityLabel"));
            activityTimeTextView.setText(getIntent().getExtras().getString("ActivityTime") + " mins");
            activityDateTextView.setText(dateFormat.format(calendar.getTime()));
        }
    }
}