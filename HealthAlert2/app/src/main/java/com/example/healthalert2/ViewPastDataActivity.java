package com.example.healthalert2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

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
    }

}