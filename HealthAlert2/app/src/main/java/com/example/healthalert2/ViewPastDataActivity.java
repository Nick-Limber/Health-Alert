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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewPastDataActivity extends AppCompatActivity {

    private LineGraph graph;
    private ListView weightList;
    private ListView nutritionList;
    private ListView workoutList;

    private OkHttpClient client = new OkHttpClient(); // HTTP client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_data);

        graph = findViewById(R.id.simpleGraph);
        weightList = findViewById(R.id.weightList);
        nutritionList = findViewById(R.id.nutritionList);
        workoutList = findViewById(R.id.workoutList);

        fetchPastDataFromBackend();
    }

    private void fetchPastDataFromBackend() {
        String url = "http://10.0.2.2:5001/health/pastdata"; //backend endpoint

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                String responseBody = response.body().string();

                try {
                    JSONObject json = new JSONObject(responseBody);

                    //weights for graph
                    JSONArray weightArray = json.getJSONArray("weights");
                    List<Float> weightValues = new ArrayList<>();
                    List<String> weightTimeStamps = new ArrayList<>();
                    List<String> weightLabels = new ArrayList<>();

                    for (int i = 0; i < weightArray.length(); i++)
                    {
                        JSONObject entry = weightArray.getJSONObject(i);
                        weightValues.add((float) entry.getDouble("weight"));
                        weightTimeStamps.add(entry.getString("recordAt"));
                        weightLabels.add(entry.getString("recordAt")); // date for label
                    }

                    //nutrition
                    JSONArray nutritionArray = json.getJSONArray("nutrition");
                    final List<String> nutritionStrings = new ArrayList<>();
                    for (int i = 0; i < nutritionArray.length(); i++)
                    {
                        JSONObject entry = nutritionArray.getJSONObject(i);
                        nutritionStrings.add(
                                entry.getString("recordAt") + " - " +
                                        entry.getInt("calories") + " cals -  " +
                                        entry.getInt("protein") + "g protein - " +
                                        entry.getInt("carbs") + "g carbs"
                        );
                    }

                    //workouts
                    JSONArray workoutArray = json.getJSONArray("exercise");
                    final List<String> workoutStrings = new ArrayList<>();
                    for (int i = 0; i < workoutArray.length(); i++)
                    {
                        JSONObject entry = workoutArray.getJSONObject(i);
                        workoutStrings.add(
                                entry.getString("exercise_type") + " - " +
                                        entry.getInt("sets") + "x" +
                                        entry.getInt("reps") + " - " +
                                        entry.getInt("weight") + " lbs"
                        );
                    }

                    // Update UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // set data
                            graph.setData(weightValues, weightTimeStamps);

                            // populate listsviw
                            weightList.setAdapter(new ArrayAdapter<>(
                                    ViewPastDataActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    weightLabels
                            ));

                            //nutrition
                            nutritionList.setAdapter(new ArrayAdapter<>(
                                    ViewPastDataActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    nutritionStrings
                            ));

                            //workout
                            workoutList.setAdapter(new ArrayAdapter<>(
                                    ViewPastDataActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    workoutStrings
                            ));
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}