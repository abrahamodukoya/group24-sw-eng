package com.group24.wellnessapp;

import android.app.Activity;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {
    private Context context = this;
    JSONObject jObj;

    // AsyncTask for JSON requests
    class analyticsAsyncTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground (String...userID) {
            // DAILY
            BarChart dailyChart = (BarChart)findViewById(R.id.barGraphDaily);
            try {
                jObj = getGraphData(LoginActivity.getUserID(), "daily");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int prodCount = -1;
            int socialCount = -1;
            int restCount = -1;
            int sleepCount = -1;
            int fitCount = -1;

            try {
                prodCount = Integer.parseInt(jObj.getString("prodCount"));
                socialCount = Integer.parseInt(jObj.getString("socialCount"));
                restCount = Integer.parseInt(jObj.getString("restCount"));
                sleepCount = Integer.parseInt(jObj.getString("sleepCount"));
                fitCount = Integer.parseInt(jObj.getString("fitCount"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dailyChart.setDrawBarShadow(false);
            dailyChart.setDrawValueAboveBar(true);
            dailyChart.setPinchZoom(false);
            dailyChart.setDrawGridBackground(false);

            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0, prodCount));
            entries.add(new BarEntry(1, socialCount));
            entries.add(new BarEntry(2, restCount));
            entries.add(new BarEntry(3, sleepCount));
            entries.add(new BarEntry(4, fitCount));

            BarDataSet set = new BarDataSet(entries, "Your Daily Activities");
            set.setColors(new int[] {R.color.teal1, R.color.teal2, R.color.teal3, R.color.teal4, R.color.teal5}, context);

            BarData data = new BarData(set);
            data.setBarWidth(0.9f);
            dailyChart.setData(data);

            String[] months = new String[]{"Productivity", "Social", "Rest", "Sleep", "Fitness"};
            XAxis xAxis = dailyChart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(months));

            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            xAxis.setGranularity(1);
            xAxis.setCenterAxisLabels(false);

            YAxis yAxis = dailyChart.getAxisLeft();
            yAxis.setAxisMinimum(0);
            yAxis.setDrawGridLines(false);
            xAxis.setDrawGridLines(false);

            dailyChart.getDescription().setEnabled(false);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            // WEEKLY
            BarChart weeklyChart = (BarChart)findViewById(R.id.barGraphWeekly);
            try {
                jObj = getGraphData(LoginActivity.getUserID(), "weekly");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                prodCount = Integer.parseInt(jObj.getString("prodCount"));
                socialCount = Integer.parseInt(jObj.getString("socialCount"));
                restCount = Integer.parseInt(jObj.getString("restCount"));
                sleepCount = Integer.parseInt(jObj.getString("sleepCount"));
                fitCount = Integer.parseInt(jObj.getString("fitCount"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            weeklyChart.setDrawBarShadow(false);
            weeklyChart.setDrawValueAboveBar(true);
            weeklyChart.setPinchZoom(false);
            weeklyChart.setDrawGridBackground(false);

            entries = new ArrayList<>();
            entries.add(new BarEntry(0, prodCount));
            entries.add(new BarEntry(1, socialCount));
            entries.add(new BarEntry(2, restCount));
            entries.add(new BarEntry(3, sleepCount));
            entries.add(new BarEntry(4, fitCount));

            set = new BarDataSet(entries, "Your Weekly Activities");
            set.setColors(new int[] {R.color.teal1, R.color.teal2, R.color.teal3, R.color.teal4, R.color.teal5}, context);

            data = new BarData(set);
            data.setBarWidth(0.9f);
            weeklyChart.setData(data);

            months = new String[]{"Productivity", "Social", "Rest", "Sleep", "Fitness"};
            xAxis = weeklyChart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(months));

            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            xAxis.setGranularity(1);
            xAxis.setCenterAxisLabels(false);

            yAxis = weeklyChart.getAxisLeft();
            yAxis.setAxisMinimum(0);
            yAxis.setDrawGridLines(false);
            xAxis.setDrawGridLines(false);

            weeklyChart.getDescription().setEnabled(false);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            // MONTHLY
            BarChart monthlyChart = (BarChart)findViewById(R.id.barGraphMonthly);
            try {
                jObj = getGraphData(LoginActivity.getUserID(), "monthly");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                prodCount = Integer.parseInt(jObj.getString("prodCount"));
                socialCount = Integer.parseInt(jObj.getString("socialCount"));
                restCount = Integer.parseInt(jObj.getString("restCount"));
                sleepCount = Integer.parseInt(jObj.getString("sleepCount"));
                fitCount = Integer.parseInt(jObj.getString("fitCount"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            monthlyChart.setDrawBarShadow(false);
            monthlyChart.setDrawValueAboveBar(true);
            monthlyChart.setPinchZoom(false);
            monthlyChart.setDrawGridBackground(false);

            entries = new ArrayList<>();
            entries.add(new BarEntry(0, prodCount));
            entries.add(new BarEntry(1, socialCount));
            entries.add(new BarEntry(2, restCount));
            entries.add(new BarEntry(3, sleepCount));
            entries.add(new BarEntry(4, fitCount));

            set = new BarDataSet(entries, "Your Monthly Activities");
            set.setColors(new int[] {R.color.teal1, R.color.teal2, R.color.teal3, R.color.teal4, R.color.teal5}, context);

            data = new BarData(set);
            data.setBarWidth(0.9f);
            monthlyChart.setData(data);

            months = new String[]{"Productivity", "Social", "Rest", "Sleep", "Fitness"};
            xAxis = monthlyChart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(months));

            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            xAxis.setGranularity(1);
            xAxis.setCenterAxisLabels(false);

            yAxis = monthlyChart.getAxisLeft();
            yAxis.setAxisMinimum(0);
            yAxis.setDrawGridLines(false);
            xAxis.setDrawGridLines(false);

            monthlyChart.getDescription().setEnabled(false);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            return null;
        }

        protected void onPostExecute (Void v) {
            // Remove back button from toolbar
            if (getSupportActionBar() != null) {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(false);
            }

            Log.d("jObj", jObj.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Start AsyncTask
        new analyticsAsyncTask().execute("text");
    }

    // Format method for x-axis of graphs
    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }

    public static JSONObject getGraphData(String userID, String type) throws IOException {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(calendar.getTime());

        // Connect to server based of type of graph, the user, and the date
        URL urlForGetRequest = new URL("http://3.92.227.189:80/api/" + type + "Stat/" + userID + "/" + currentDate);
        String readLine = null;
        HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
        connection.setRequestMethod("GET");

        // Get response
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            } in.close();

            JSONObject jObj = null;

            try {
                 jObj = new JSONObject(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jObj;
        } else {
            // Error message
            Log.d("Graph get error", "GET NOT WORKED");
            return null;
        }
    }
}