package com.example.farmingservice;

import android.util.Log;

import com.google.gson.Gson;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
public class Payload {
    private String description;
    private double latitude;
    private double longitude;
    private String organizationId;
    private String machineId;
    private String date;
    private String imageData;

    public Payload(String description, double latitude, double longitude, String organizationId, String machineId, String date, String imageData) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.organizationId = organizationId;
        this.machineId = machineId;
        this.imageData = imageData;
        this.date = date;
    }

    public String getString() {
        String result;

        Gson gson = new Gson();
        result = gson.toJson(this);
        Log.i("Payload", result);

        return result;
    }
}
