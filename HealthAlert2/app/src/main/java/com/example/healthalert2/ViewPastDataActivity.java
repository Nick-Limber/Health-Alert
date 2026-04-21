package com.example.healthalert2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewPastDataActivity extends AppCompatActivity {

    private LineGraph graph;
    private ListView weightList;
    private ListView nutritionList;
    private ListView workoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_data);

        graph = findViewById(R.id.simpleGraph);
        weightList = findViewById(R.id.weightList);
        nutritionList = findViewById(R.id.nutritionList);
        workoutList = findViewById(R.id.workoutList);

        //set up nav bar
        setUpNavBar();

        //temp data
        //showSampleData();

        fetchPastDataFromBackend();
    }

    /*private void showSampleData()
    {
        //sample weight
        List<Float> weightValues = new ArrayList<>();
        List<String> weightTimeStamps = new ArrayList<>();
        List<String> weightLabels = new ArrayList<>();

        weightValues.add(200f);
        weightValues.add(190f);
        weightValues.add(187f);

        weightTimeStamps.add("2026-03-01 10:30:33");
        weightTimeStamps.add("2026-03-02 11:12:55");
        weightTimeStamps.add("2026-03-03 09:50:27");

        weightLabels.add("March 1");
        weightLabels.add("March 2");
        weightLabels.add("March 3");

        graph.setData(weightValues, weightTimeStamps);

        weightLabels.clear();

        for (int i = 0; i < weightValues.size(); i++)
        {
            String rawTime = weightTimeStamps.get(i);
            String day;

            try {
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd");
                Date date = dbFormat.parse(rawTime);
                day = displayFormat.format(date);
            } catch (Exception e) {
                day = rawTime;
            }

            weightLabels.add(day + ": " + weightValues.get(i) + " lbs");
        }

        weightList.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, weightLabels
        ));

        //nutrition
        List<String> nutritionString = new ArrayList<>();
        nutritionString.add("March 1 - Keto - 2500 cals - 100g protein - 0g carbs");
        nutritionString.add("March 2 - Low Cal - 1500 cals - 90g protein - 70g carbs");

        nutritionList.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, nutritionString
        ));

        //exercise
        List<String> workoutStrings = new ArrayList<>();
        workoutStrings.add("Bench Press - 5x5 - 225 lbs");
        workoutStrings.add("Squat - 3x8 - 175 lbs");

        workoutList.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1,workoutStrings
        ));
    }*/

    private void setUpNavBar() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_past_data);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, HomePage.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_forum) {
                Intent intent = new Intent(this, CommunityForumActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_account) {
                Intent intent = new Intent(this, AccountPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.workout_plan) {
                Intent intent = new Intent(this, WorkoutPlanActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_past_data) {
                return false;
            }
            return false;
        });
    }

    private void fetchPastDataFromBackend() {
        int loggedInUserId = 1;
        String url = "https://gleaming-sparkle-production-acb6.up.railway.app/"; //backend endpoint

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                respone -> {
                    try {
                        JSONObject json = new JSONObject(respone);

                        //weight
                        JSONArray weightArray = json.getJSONArray("weights");
                        List<Float> weightValues = new ArrayList<>();
                        List<String> weightTimeStamps = new ArrayList<>();
                        List<String> weightLabels = new ArrayList<>();

                        for (int i = 0; i < weightArray.length(); i++) {
                            JSONObject entry = weightArray.getJSONObject(i);

                            float weight = (float) entry.getDouble("weight");
                            String rawTime = entry.getString("recorded_at");

                            String cleanDate = formatDate(rawTime);

                            weightValues.add(weight);
                            weightTimeStamps.add(cleanDate);

                            weightLabels.add(cleanDate + ": " + weight + " lbs");
                        }

                        graph.setData(weightValues, weightTimeStamps);

                        weightList.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_list_item_1,
                                weightLabels
                        ));

                        //nutrition
                        JSONArray nutritionArray = json.getJSONArray("nutrition");
                        List<String> nutritionStrings = new ArrayList<>();

                        for (int i = 0; i < nutritionArray.length(); i++) {
                            JSONObject entry = nutritionArray.getJSONObject(i);

                            nutritionStrings.add(
                                    formatDate(entry.getString("recorded_at")) + " - " +
                                            entry.getString("diet_name") + " - " +
                                            entry.getInt("calories") + " cals - " +
                                            entry.getInt("protein") + "g protein - " +
                                            entry.getInt("carbohydrates") + "g carbs"
                            );
                        }

                        nutritionList.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_list_item_1,
                                nutritionStrings
                        ));

                        //workouts
                        JSONArray workoutArray = json.getJSONArray("exercise");
                        List<String> workoutStrings = new ArrayList<>();

                        for (int i = 0; i < workoutArray.length(); i++) {
                            JSONObject entry = workoutArray.getJSONObject(i);

                            workoutStrings.add(
                                    formatDate(entry.getString("recorded_at")) + " - " +
                                            entry.getString("exercise_type") + " - " +
                                            entry.getInt("sets") + "x" +
                                            entry.getInt("reps") + " - " +
                                            entry.getInt("weight") + " lbs"
                            );
                        }

                        workoutList.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_list_item_1,
                                workoutStrings
                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        android.widget.Toast.makeText(ViewPastDataActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    android.widget.Toast.makeText(ViewPastDataActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                }

        );
        queue.add(request);
    }

    private String formatDate(String rawTime)
    {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dbFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd");

            Date date = dbFormat.parse(rawTime);
            return displayFormat.format(date);
        }catch (Exception e) {
            return rawTime.split("T")[0];
        }
    }
}