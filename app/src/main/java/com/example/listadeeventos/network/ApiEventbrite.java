package com.example.listadeeventos.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiEventbrite {
    private static final String BASE_URL = "http://10.0.2.2:5000"; // localhost para el emulador

    public static String getEventos() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + "/eventos")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
