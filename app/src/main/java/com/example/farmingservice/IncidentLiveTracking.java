package com.example.farmingservice;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IncidentLiveTracking extends AppCompatActivity {

    private TextView textView;
    private TextView statusTextView;
    private OkHttpClient client = new OkHttpClient();
    private static final String GET_GEO_INFO = "https://api.openrouteservice.org/directions?api_key=58d904a497c67e00015b45fc0ecaf57e78f14f08a68ee400d004a426&coordinates=8.477079," +
            "%2049.469316%7C8.450546,%2049.420801&profile=driving-car&format=geojson";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_live_tracking);
        textView = (TextView) findViewById(R.id.incident);
        statusTextView = (TextView) findViewById(R.id.statusOverView);
        CharSequence text = getIntent().getStringExtra("incidentId");
        if (text != null) {
            textView.setText(text);
        }
        WebView webView = (WebView) findViewById(R.id.mapview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://maps.openrouteservice.org/directions?n1=48.983189&n2=8.481973&n3=10&a=49.129296,8.557958,48.815367,8.481802&b=0&c=0&k1=en-US&k2=km");
        getRequestToMapsApi();
    }

    public void getRequestToMapsApi() {
        Request request = new Request.Builder()
                .url(GET_GEO_INFO)
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
                Log.i("IncidentReport response message", jsonData);
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray jsonArray = jsonObject.getJSONArray("features");
                Log.i("json features object: ", jsonArray.toString());
                JSONObject propertiesObject = (JSONObject) jsonArray.getJSONObject(0).get("properties");
                JSONArray summaryObject = (JSONArray) propertiesObject.get("summary");
                String duration = summaryObject.getJSONObject(0).get("duration").toString();
                String distance = summaryObject.getJSONObject(0).get("distance").toString();
                Log.i("distance and duration: ",distance+", "+duration);
                runOnUiThread(() -> statusTextView.setText("The service personnal is 48kms away " +
                        "and will be at your loaction by 58mins"));

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
