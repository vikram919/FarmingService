package com.example.farmingservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IncidentTracker extends AppCompatActivity {

    private static final String GET_INCIDENTS = "https://hackday.genie-enterprise.com/incident/organization/johndeere";
    OkHttpClient client = new OkHttpClient();
    private ArrayList<String> incidentReportList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView incidentReportsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_tracker);
        incidentReportsView = (ListView) findViewById(R.id.list_view);
        sendGetRequest();

        incidentReportsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String incidentId = incidentReportsView.getItemAtPosition(position).toString();
                Log.i("incident id: ", incidentId);
                Intent incidentLiveTracking = new Intent(getApplicationContext(), IncidentLiveTracking.class);
                incidentLiveTracking.putExtra("incidentId", incidentId);
                startActivity(incidentLiveTracking);
            }
        });
    }

    public void sendGetRequest() {
        Request request = new Request.Builder()
                .url(GET_INCIDENTS)
                .build();
        new MyAsyncTask().execute(request);
    }
    class MyAsyncTask extends AsyncTask<Request, Void, Response> {

        @Override
        protected Response doInBackground(Request... requests) {
            Response response = null;
            try {
                response = client.newCall(requests[0]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String jsonData = response.body().string();
                Log.i("IncidentTracking response message", jsonData);
                JSONArray jarray = new JSONArray(jsonData);
                incidentReportList = new ArrayList<>(jarray.length());
                for (int i = 0; i < jarray.length(); i++) {
                    Object tractorName = jarray.getJSONObject(i).get("id");
                    incidentReportList.add(tractorName.toString());
                }
                runOnUiThread(() -> {
                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.list_item, R.id.product_name, incidentReportList);
                    incidentReportsView.setAdapter(arrayAdapter);
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Log.i("IncidentReport response code is: ", " " + response.code());
        }
    }
}
