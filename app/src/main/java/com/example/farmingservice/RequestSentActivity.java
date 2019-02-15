package com.example.farmingservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;

import lombok.Builder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestSentActivity extends AppCompatActivity {

    private ImageView imageView;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    private Button sendButton;
    private static final String CURRENT_TIME_ZONE_ID = "Europe/Berlin";
    public static final String SERVER_ADDRESS = "https://hackday.genie-enterprise.com/incident";
    private String encodedImage;
    private LinearLayout linearLayout;
    private TextView textView;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_sent);
        imageView = (ImageView) findViewById(R.id.imageview);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        textView = (TextView) findViewById(R.id.textView);
        Bundle bundle = getIntent().getBundleExtra("imageData");
        String imageUriString = bundle.getString("uri");
        Bitmap bitmap = null;
        if (imageUriString == null) {
            bitmap = (Bitmap) bundle.get("data");
        } else {
            try {
                Uri selectedImage = Uri.parse(bundle.getString("uri"));
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            imageView.setImageBitmap(bitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            this.encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    public void post(View view) throws IOException, JSONException {
        Payload payload = new Payload("My tractor is broken!!", 109.19, 102.2, "1736-4746", "1874-4748", createCurrentTimeStamp(), encodedImage);
        RequestBody body = RequestBody.create(JSON, payload.getString());
        Request request = new Request.Builder()
                .url(SERVER_ADDRESS)
                .post(body)
                .build();
        new MyAsyncTask().execute(request);
        linearLayout.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
    }
}
