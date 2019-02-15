package com.example.farmingservice;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
        String result = "{\n" +
                " \"date\": \"2019-02-15T18:27:11.814Z\",\n" +
                " \"description\": \"string\",\n" +
                " \"id\": \"string\",\n" +
                " \"image\":\"" +imageData+"\",\n" +
                " \"latitude\":"+ 123.09+",\n" +
                " \"longitude\":"+ 124.09+",\n" +
                " \"machineId\": \"string\",\n" +
                " \"oraganizationId\": \"string\"\n" +
                "}";
        Log.i("Payload", result);
        return result;
    }
}
