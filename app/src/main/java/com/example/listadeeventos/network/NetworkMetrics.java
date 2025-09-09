package com.example.listadeeventos.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkMetrics implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.nanoTime();

        Response response = chain.proceed(request);

        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        long responseSize = response.body() != null ? response.body().contentLength() : 0;

        Performance_Monitoring.getInstance().logHttpCall(
                request.url().toString(),
                duration,
                response.code(),
                response.code(),
                request.method()
        );

        return response;
    }
}
