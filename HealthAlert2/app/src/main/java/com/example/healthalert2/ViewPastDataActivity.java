package com.example.healthalert2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewPastDataActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_past_data);

            ListView listView = findViewById(R.id.listViewPastData);

            String[] sampleData = new String[]{
                "Entry 1: 1/21/2026",
                "Entry 2: 2/13/2026",
                "Entry 3: 2/20/2026"
            };

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                sampleData
            );

            listView.setAdapter(adapter);
    }

}