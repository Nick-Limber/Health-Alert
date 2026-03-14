package com.example.healthalert2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewPastDataActivity extends AppCompatActivity
{
    LineGraph graph;
    ListView weightList;
    ListView nutritionList;
    ListView workoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_data);

        graph = findViewById(R.id.simpleGraph);
        weightList = findViewById(R.id.weightList);
        nutritionList = findViewById(R.id.nutritionList);
        workoutList = findViewById(R.id.workoutList);

        //ALL SAMPLE DATA
        graph.setData(Arrays.asList(180f, 178f, 176f, 175f));

        //sample data - weight
        String[] sampleWeight = {
                "Jan 1 - 200 lbs",
                "Jan 10 - 190 lbs",
                "Jan 25 - 180 lbs",
                "Feb 3 - 185 lbs"
        };
        weightList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sampleWeight));

        //sample data - nutrition
        String[] sampleNutrition = {
                "Feb 4 - 2500 calories / 150g protein / 175 carbs",
                "Feb 7 - 2100 calories / 180g protein / 150 carbs",
                "Feb 16 - 2200 calories / 120g protein / 200 carbs",
                "Feb 27 - 2100 calories / 190g protein / 140 carbs"
        };
        nutritionList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sampleNutrition));

        //sample data - exercise
        String[] sampleWorkout = {
                "Bench Press - 4x12 - 225 lbs",
                "Squat - 5x10 - 350 lbs",
                "Deadlift - 3x3 - 300 lbs",
                "Shoulder Press - 8x8 - 185 lbs"
        };
        workoutList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sampleWorkout));

        //Fetch live data
        fetchData();
    }

    private void fetchData(){
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:5001/health/pastdata";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
                    public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
                    public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                         JSONArray dataArray = new JSONArray(responseBody);

                         List<String> weightData = new ArrayList<>();
                         List<Float> weightDataGraph = new ArrayList<>();
                         List<String> nutritionData = new ArrayList<>();
                         List<String> workoutData = new ArrayList<>();

                         for (int i = 0; i < dataArray.length(); i++) {
                             JSONObject entry = dataArray.getJSONObject(i);

                             String date = entry.getString("date");

                             //weight
                             int weight = entry.optInt("weight", 0);
                             weightData.add(date + " - " + weight + " lbs");
                             if (weight != 0) {
                                 weightDataGraph.add((float) weight);
                             }

                             //nutrition
                             int calories = entry.optInt("calories", 0);
                             int protein = entry.optInt("protein", 0);
                             int carbs = entry.optInt("carbs", 0);
                             nutritionData.add(date + " - " + calories + " calories / " + protein + "g protein / " + carbs + "g carbs");

                             //exercise
                             String exercise = entry.optString("exercise", "");
                             String sets = entry.optString("sets", "");
                             String reps = entry.optString("reps", "");
                             String exWeight = entry.optString("weight", "");
                             workoutData.add(exercise + " - " + sets + "x" + reps + " - " + exWeight);
                         }

                         runOnUiThread(() -> {
                             weightList.setAdapter(new ArrayAdapter<>(ViewPastDataActivity.this, android.R.layout.simple_list_item_1, weightData));
                             nutritionList.setAdapter(new ArrayAdapter<>(ViewPastDataActivity.this, android.R.layout.simple_list_item_1, nutritionData));
                             workoutList.setAdapter(new ArrayAdapter<>(ViewPastDataActivity.this, android.R.layout.simple_list_item_1, workoutData));
                             graph.setData(weightDataGraph);
                         });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

}