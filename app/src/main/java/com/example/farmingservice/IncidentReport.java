package com.example.farmingservice;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IncidentReport extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int CAPTURE_IMAGE = 2;
    OkHttpClient client = new OkHttpClient();
    public static final String SERVER_GET_ADDRESS = "https://hackday.genie-enterprise.com/machine/organization/johndeere";
    private ArrayList<String> userMachinesList;
    private String[] userMachines;
    private ListView userMachinesListView;
    private EditText editText;
    private ArrayAdapter<String> arrayAdapter;
    private TextView machineSelectedTextView;
    private ImageButton cancelMachineTypeButton;
    private ArrayList<String> machinePartsList;
    private TextView machinePartTextView;
    private ImageButton cancelMachinePartButton;
    private String currentSelectedMachine = "machine";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report);
        userMachinesListView = (ListView) findViewById(R.id.list_view);
        editText = (EditText) findViewById(R.id.inputSearch);
        machineSelectedTextView = (TextView) findViewById(R.id.machineType);
        cancelMachineTypeButton = (ImageButton) findViewById(R.id.cancelMachineType);
        machinePartTextView = (TextView) findViewById(R.id.machinePartType);
        cancelMachinePartButton = (ImageButton) findViewById(R.id.cancelMachinePartType);
        try {
            sendGetRequest();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                arrayAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        userMachinesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = userMachinesListView.getItemAtPosition(position).toString();
                if (machinePartsList.contains(selectedOption)) {
                    machinePartTextView.setText(selectedOption);
                    machinePartTextView.setVisibility(View.VISIBLE);
                    cancelMachinePartButton.setVisibility(View.VISIBLE);
                    return;
                }
                currentSelectedMachine = selectedOption;
                machineSelectedTextView.setText(currentSelectedMachine);
                machineSelectedTextView.setVisibility(View.VISIBLE);
                cancelMachineTypeButton.setVisibility(View.VISIBLE);
                // editText.setText(userMachinesListView.getItemAtPosition(position).toString());
                editText.setHint(currentSelectedMachine + " parts...");
                runOnUiThread(() -> {
                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.list_item, R.id.product_name, machinePartsList);
                    userMachinesListView.setAdapter(arrayAdapter);
                });
            }
        });
    }

    public void onClearMachineName(View view) {
        machineSelectedTextView.setVisibility(View.INVISIBLE);
        cancelMachineTypeButton.setVisibility(View.INVISIBLE);
        editText.setHint("Search your machines..");
        machinePartTextView.setVisibility(View.INVISIBLE);
        cancelMachinePartButton.setVisibility(View.INVISIBLE);
        runOnUiThread(() -> {
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                    R.layout.list_item, R.id.product_name, userMachines);
            userMachinesListView.setAdapter(arrayAdapter);
        });
    }

    public void onClearMachinePartName(View view) {
        machinePartTextView.setVisibility(View.INVISIBLE);
        cancelMachinePartButton.setVisibility(View.INVISIBLE);
        editText.setHint(currentSelectedMachine + " parts..");
    }

    public void startGalleryIntent(View view) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void startCamera(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAPTURE_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Uri selectedImage = data.getData();
                    extras.putString("uri", selectedImage.toString());
                    Intent requestSentActivity = new Intent(this, RequestSentActivity.class);
                    requestSentActivity.putExtra("imageData", extras);
                    startActivity(requestSentActivity);
                }
            }
        }

        if (requestCode == CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Intent requestSentActivity = new Intent(this, RequestSentActivity.class);
                    requestSentActivity.putExtra("imageData", extras);
                    startActivity(requestSentActivity);
                }
            }
        }
    }

    public void sendGetRequest() throws IOException, JSONException {
        Request request = new Request.Builder()
                .url(SERVER_GET_ADDRESS)
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
                JSONArray jarray = new JSONArray(jsonData);
                userMachinesList = new ArrayList<>(jarray.length());
                for (int i = 0; i < jarray.length(); i++) {
                    Object tractorName = jarray.getJSONObject(i).get("name");
                    if (tractorName.equals("Tractor 8245R")) {
                        JSONArray machinePartsJsonArray = jarray.getJSONObject(i).getJSONArray("parts");
                        machinePartsList = new ArrayList<>(machinePartsJsonArray.length());
                        for (int j = 0; j < machinePartsJsonArray.length(); j++) {
                            machinePartsList.add((String) machinePartsJsonArray.getJSONObject(j).get("name"));
                        }
                    }
                    userMachinesList.add(tractorName.toString());
                }
                userMachines = userMachinesList.toArray(new String[userMachinesList.size()]);
                runOnUiThread(() -> {
                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.list_item, R.id.product_name, userMachines);
                    userMachinesListView.setAdapter(arrayAdapter);
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
