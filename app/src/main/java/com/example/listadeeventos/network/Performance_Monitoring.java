package com.example.listadeeventos.network;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Performance_Monitoring {

    private static Performance_Monitoring instance;
    private Map<String, Long> traceStartTimes;

    private Performance_Monitoring() {
        traceStartTimes = new HashMap<>();
    }

    public static synchronized Performance_Monitoring getInstance() {
        if (instance == null) {
            instance = new Performance_Monitoring();
        }
        return instance;
    }

    // Métodos genéricos
    public void startTrace(String traceName) {
        traceStartTimes.put(traceName, System.nanoTime());
        Log.d("Performance", "🚀 Iniciando: " + traceName);
    }

    public void endTrace(String traceName, Map<String, Object> metadata) {
        Long startTime = traceStartTimes.get(traceName);
        if (startTime == null) return;

        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        Log.d("Performance", "✅ " + traceName + " completado en " + duration + "ms");

        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            Log.d("Performance", "   📊 " + entry.getKey() + ": " + entry.getValue());
        }

        traceStartTimes.remove(traceName);
    }

    // ================== PARA LOGIN (SQLite) ==================
    public void startLoginFlowTrace() {
        startTrace("trace_login_flow");
    }

    public void endLoginFlowTrace(boolean success, long duration, String username) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("success", success);
        metadata.put("duration_ms", duration);
        metadata.put("username", username);
        metadata.put("source", "database");
        endTrace("trace_login_flow", metadata);
    }

    public void endLoginFlowTrace(boolean success, long duration, String username, String error) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("success", success);
        metadata.put("duration_ms", duration);
        metadata.put("username", username);
        metadata.put("error", error);
        metadata.put("source", "database");
        endTrace("trace_login_flow", metadata);
    }

    // ================== PARA LISTA_EVENTOS (API) ==================
    public void startListLoadTrace() {
        startTrace("trace_list_load");
    }

    public void endListLoadTrace(int itemCount, long duration, int statusCode, long responseSize) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("item_count", itemCount);
        metadata.put("duration_ms", duration);
        metadata.put("http_status", statusCode);
        metadata.put("response_size_bytes", responseSize);
        metadata.put("source", "api");
        endTrace("trace_list_load", metadata);
    }

    public void endListLoadTraceWithError(long duration, String error, int statusCode) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("duration_ms", duration);
        metadata.put("error", error);
        metadata.put("http_status", statusCode);
        metadata.put("source", "api");
        endTrace("trace_list_load", metadata);
    }

    // Métricas HTTP individuales (para el interceptor)
    public void logHttpCall(String url, long duration, int statusCode, long responseSize, String method) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("url", url);
        metadata.put("duration_ms", duration);
        metadata.put("status_code", statusCode);
        metadata.put("response_size_bytes", responseSize);
        metadata.put("http_method", method);

        Log.d("Performance", "🌐 HTTP: " + method + " " + url + " → " +
                statusCode + " (" + duration + "ms, " + responseSize + " bytes)");
    }
}