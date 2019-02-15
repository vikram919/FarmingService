package com.example.farmingservice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IncidentReport extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView loadedImage;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    private Button sendButton;
    private static final String CURRENT_TIME_ZONE_ID = "Europe/Berlin";
    public static final String SERVER_ADDRESS = "https://hackday.genie-enterprise.com/incident";
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report);
        loadedImage = (ImageView) findViewById(R.id.imageview);
        sendButton = (Button) findViewById(R.id.sendToServer);
    }

    public void startGalleryIntent(View view) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void post(View view) throws IOException {
        Payload payload = new Payload("My tractor is broken!!", 109.19, 102.2, "1736-4746", "1874-4748", createCurrentTimeStamp(), encodedImage);
        Log.i("test payload: ", payload.toString());
        RequestBody body = RequestBody.create(JSON, payload.getString());
        Request request = new Request.Builder()
                .url(SERVER_ADDRESS)
                .post(body)
                .build();
        new MyAsyncTask().execute(request);
        Intent requestSentActivity = new Intent(this, RequestSentActivity.class);
        startActivity(requestSentActivity);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Uri selectedImage = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                        loadedImage.setImageBitmap(bitmap);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        byteArrayOutputStream.close();
                        this.encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        sendButton.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            // do nothing
        }
    }

    public static String createCurrentTimeStamp() {
        return Instant.now().atZone(ZoneId.of(CURRENT_TIME_ZONE_ID)).withFixedOffsetZone().toString();
    }
}
