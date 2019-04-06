package com.group24.wellnessapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.w3c.dom.Text;

public class AdviceActivity extends AppCompatActivity {
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        // Create and display graphs
        for (int i = 0; i < 3; i++) {
            LinearLayout parentLinearLayout = findViewById(R.id.linearLayout);

            LinearLayout newLayout = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = 25;
            layoutParams.leftMargin = 30;
            layoutParams.rightMargin = 30;
            newLayout.setLayoutParams(layoutParams);
            newLayout.setBackgroundResource(R.drawable.customborder);
            newLayout.setOrientation(LinearLayout.VERTICAL);
            int newLayoutID = 2;
            newLayout.setId(newLayoutID);

            TextView adviceTextView = new TextView(context);
            switch(i) {
                case 0:
                    adviceTextView.setText("Your Productivity time is much higher than normal. Your Rest is also quite low, make sure you take a break.");
                    break;
                case 1:
                    adviceTextView.setText("Your Fitness time is very low. Try to stay active!");
                    break;
                case 2:
                    adviceTextView.setText("You only got 5 hours sleep yesterday. It is recommended you get 8 hours. Try to get more tonight.");
                    break;
            }
            adviceTextView.setTextSize(17);

            TextView titleTextView = new TextView(context);
            titleTextView.setText("Tip!");
            titleTextView.setTextSize(19);

            LinearLayout responseLayout = new LinearLayout(context);
            LinearLayout.LayoutParams responseLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            responseLayout.setLayoutParams(responseLayoutParams);
            responseLayout.setOrientation(LinearLayout.HORIZONTAL);
            int responseLayoutID = 1;
            responseLayout.setId(responseLayoutID);

            TextView wasThisHelpful = new TextView(context);
            wasThisHelpful.setText("Was this helpful?  ");
            TextView yesResponse = new TextView(context);
            int yesViewID = 300;
            yesResponse.setId(yesViewID);
            TextView noResponse = new TextView(context);
            int noViewID = 200;
            noResponse.setId(noViewID);
            TextView space = new TextView(context);
            yesResponse.setText(Html.fromHtml("<u>" + "Yes" + "<u>"));
            yesResponse.setTextSize(15);
            space.setText("   ");
            noResponse.setText(Html.fromHtml("<u>" + "No" + "<u>"));
            noResponse.setTextSize(15);

            responseLayout.addView(wasThisHelpful);
            responseLayout.addView(yesResponse);
            responseLayout.addView(space);
            responseLayout.addView(noResponse);

            newLayout.addView(titleTextView);
            newLayout.addView(adviceTextView);
            newLayout.addView(responseLayout);

            parentLinearLayout.addView(newLayout);
        }

        int yesID = 300;
        final TextView yesResponse = findViewById(yesID);
        int responseLayoutID = 1;
        final LinearLayout responseLayout = findViewById(responseLayoutID);
        yesResponse.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                responseLayout.setVisibility(View.GONE);
            }
        });
        int noID = 200;
        TextView noResponse = findViewById(noID);
        int newLayoutID = 2;
        final LinearLayout newLayout = findViewById(newLayoutID);
        noResponse.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                newLayout.setVisibility(View.GONE);
            }
        });
    }
}