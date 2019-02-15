package com.example.farmingservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // start incident report activity
    public void startIncidentReportActivity(View view) {
        Intent incidentIntent = new Intent(MainActivity.this, IncidentReport.class);
        startActivity(incidentIntent);
    }
}
