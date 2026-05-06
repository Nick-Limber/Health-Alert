package com.example.healthalert2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthalert2.data.network.AllHistoryResponse;
import com.example.healthalert2.data.network.ExerciseEntry;
import com.example.healthalert2.data.network.NutritionEntry;
import com.example.healthalert2.data.network.RetrofitClient;
import com.example.healthalert2.data.network.WeightEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.healthalert2.data.network.RetrofitClient;
import com.example.healthalert2.data.network.AllHistoryResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        fetchPastDataFromBackend();
    }


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

    private void fetchPastDataFromBackend()
    {
        android.content.SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");
        String authHeader = "Bearer " + token;

        Call<AllHistoryResponse> call = RetrofitClient.INSTANCE.getHealthApiService().getAllHistory(authHeader);

        call.enqueue(new Callback<AllHistoryResponse>() {
            @Override
            public void onResponse(retrofit2.Call<AllHistoryResponse> call, retrofit2.Response<AllHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null)
                {
                    AllHistoryResponse data = response.body();

                    //process weights
                    List<Float> weightValues = new ArrayList<>();
                    List<String> weightTimeStamps = new ArrayList<>();
                    List<String> weightLabels = new ArrayList<>();

                    for (WeightEntry entry : data.getWeights())
                    {
                        weightValues.add((float) entry.getWeight());
                        String cleanDate = formatDate(entry.getRecorded_at());
                        weightTimeStamps.add(cleanDate);
                        weightLabels.add(cleanDate + ": " + entry.getWeight() + " lbs");
                    }

                    graph.setData(weightValues, weightTimeStamps);
                    weightList.setAdapter(new ArrayAdapter<>(ViewPastDataActivity.this,
                            android.R.layout.simple_list_item_1, weightLabels));

                    //process nutrition
                    List<String> nutritionStrings = new ArrayList<>();
                    for (NutritionEntry entry : data.getNutrition())
                    {
                        nutritionStrings.add(formatDate(entry.getRecorded_at()) + " - " + entry.getDiet_name() +
                                " (" + entry.getCalories() + " cals - " +entry.getProtein() + " g protein - " + entry.getCarbohydrates() + " g carbs)");
                    }

                    nutritionList.setAdapter(new ArrayAdapter<>(ViewPastDataActivity.this,
                            android.R.layout.simple_list_item_1, nutritionStrings));

                    //process exercise
                    List<String> exerciseStrings = new ArrayList<>();
                    for (ExerciseEntry entry : data.getExercise())
                    {
                        exerciseStrings.add(formatDate(entry.getRecorded_at()) + " - " + entry.getExercise_type() +
                                " - " + entry.getSets() + "x" + entry.getReps() + " at " + entry.getWeight() + " lbs");
                    }
                    workoutList.setAdapter(new ArrayAdapter<>(ViewPastDataActivity.this,
                            android.R.layout.simple_list_item_1, exerciseStrings));
                }
            }
            @Override
            public void onFailure(retrofit2.Call<AllHistoryResponse> call, Throwable t)
            {
                Log.e("NETWORK_ERROR", "Failed to fetch history", t);
                Toast.makeText(ViewPastDataActivity.this, "Network Error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private String formatDate(String rawTime)
    {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            dbFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd", Locale.US);

            Date date = dbFormat.parse(rawTime);
            return displayFormat.format(date);
        }catch (Exception e) {
            return rawTime.contains("T") ? rawTime.split("T")[0] : rawTime;
        }
    }
}