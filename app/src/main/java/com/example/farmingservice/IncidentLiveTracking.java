package com.example.farmingservice;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IncidentLiveTracking extends AppCompatActivity {

    private TextView textView;
    private OkHttpClient client = new OkHttpClient();
    private static final String GET_SERVICE_PERSONAL_DATA = "https://hackday.genie-enterprise.com/engineer/engineerEta/";
    private static final String GET_INCIDENT_DATA = "https://hackday.genie-enterprise.com/incident/";
    private TextView personnelNameView;
    private TextView estimatedArrivalView;
    private TextView distanceToDestinationView;
    private String thisIncident;
    private String incidentLatitude;
    private String incidentLongitude;
    private String personnelLatitude;
    private String personnelLongitude;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_live_tracking);
        textView = (TextView) findViewById(R.id.incident);
        personnelNameView = (TextView) findViewById(R.id.personalName);
        estimatedArrivalView = (TextView) findViewById(R.id.estimatedArrivalTime);
        distanceToDestinationView = (TextView) findViewById(R.id.distanceFromDestination);
        thisIncident = getIntent().getStringExtra("incidentId");
        webView = (WebView) findViewById(R.id.mapview);
        if (thisIncident != null) {
            textView.setText(thisIncident);
            getIncidentInfo();
        }
    }

    public void getPersonnelInfo() {
        Request request = new Request.Builder()
                .url(GET_SERVICE_PERSONAL_DATA+thisIncident)
                .build();
        new GetPersonnelDataTask().execute(request);
    }

    public void getIncidentInfo() {
        Request request = new Request.Builder()
                .url(GET_INCIDENT_DATA+thisIncident)
                .build();
        new GetIncidentDataTask().execute(request);
    }

    class GetPersonnelDataTask extends AsyncTask<Request, Void, Response> {

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
                Log.i("IncidentReport response message", jsonData);
                JSONObject jsonObject = new JSONObject(jsonData);
                String personnelName = jsonObject.get("engineerName").toString();
                String duration = jsonObject.get("duration").toString();
                String distance = jsonObject.get("distance").toString();
                personnelLatitude = jsonObject.get("latitude").toString();
                personnelLongitude= jsonObject.get("longitude").toString();
                runOnUiThread(() -> {
                    webView.getSettings().setJavaScriptEnabled(true);
                    String url = getUrl();
                    webView.loadUrl(url);
                    personnelNameView.setText("Personnel incharge: "+personnelName);
                    estimatedArrivalView.setText("Estimated time of arrival: "+duration+" minutes");
                    distanceToDestinationView.setText("Distance to destination: "+distance+" kms");
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

    class GetIncidentDataTask extends AsyncTask<Request, Void, Response> {

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
                Log.i("IncidentReport response message", jsonData);
                JSONObject jsonObject = new JSONObject(jsonData);
                incidentLatitude = jsonObject.get("latitude").toString();
                incidentLongitude =jsonObject.get("longitude").toString();
                runOnUiThread(() -> {
                    getPersonnelInfo();
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

    private String getUrl() {
        return "http://maps.openrouteservice.org/directions?n1="+personnelLatitude+"&n2="+personnelLongitude+"&n3=13&a=49.471298,8.480237,"+incidentLatitude+","+incidentLongitude+"&b=0&c=0&k1=en-US&k2=km";
    }

    public void refreshPersonnelnfo(View view){
        getPersonnelInfo();
    }
}
