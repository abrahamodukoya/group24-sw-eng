package com.group24.wellnessapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

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
    }
}