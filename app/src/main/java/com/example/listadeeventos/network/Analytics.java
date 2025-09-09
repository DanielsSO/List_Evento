package com.example.listadeeventos.network;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class Analytics {

    private static Analytics instance;

    private Analytics() {
        // Inicialización si es necesaria
    }

    public static synchronized Analytics getInstance() {
        if (instance == null) {
            instance = new Analytics();
        }
        return instance;
    }

    // Método principal para tracking de eventos
    public void trackEvent(String eventName, Map<String, Object> params) {
        StringBuilder logMessage = new StringBuilder("📊 Analytics: " + eventName);

        if (params != null && !params.isEmpty()) {
            logMessage.append(" - Parámetros: ");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                logMessage.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
            }
        }

        Log.d("Analytics", logMessage.toString());
    }

    // ================== EVENTOS CLAVE ==================

    // 1. App Launch
    public void trackAppLaunch(String source, String osVersion, String appVersion) {
        Map<String, Object> params = new HashMap<>();
        params.put("source", source);
        params.put("os_version", osVersion);
        params.put("app_version", appVersion);
        trackEvent("app_launch", params);
    }

    // 2. Login Success
    public void trackLoginSuccess(String username, long latencyMs, String authMethod) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("latency_ms", latencyMs);
        params.put("auth_method", authMethod);
        trackEvent("login_success", params);
    }

    // 3. Login Fail
    public void trackLoginFail(String username, String errorCode, int attemptCount) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("error_code", errorCode);
        params.put("attempt_count", attemptCount);
        trackEvent("login_fail", params);
    }

    // 4. Screen View
    public void trackScreenView(String screenName, String previousScreen, long viewDurationMs) {
        Map<String, Object> params = new HashMap<>();
        params.put("screen_name", screenName);
        params.put("previous_screen", previousScreen);
        params.put("view_duration_ms", viewDurationMs);
        trackEvent("screen_view", params);
    }

    // 5. List View
    public void trackListView(int itemCount, String loadSource, long latencyMs) {
        Map<String, Object> params = new HashMap<>();
        params.put("item_count", itemCount);
        params.put("load_source", loadSource);
        params.put("latency_ms", latencyMs);
        trackEvent("list_view", params);
    }

    // 6. Item Detail View
    public void trackItemDetailView(String itemId, String itemType, String source) {
        Map<String, Object> params = new HashMap<>();
        params.put("item_id", itemId);
        params.put("item_type", itemType);
        params.put("source", source);
        trackEvent("item_detail_view", params);
    }

    // 7. Camera Capture
    public void trackCameraCapture(String cameraType, long captureTimeMs) {
        Map<String, Object> params = new HashMap<>();
        params.put("camera_type", cameraType);
        params.put("capture_time_ms", captureTimeMs);
        trackEvent("camera_capture", params);
    }

    // 8. Photo Uploaded
    public void trackPhotoUploaded(String itemId, long fileSizeKb, long uploadDurationMs) {
        Map<String, Object> params = new HashMap<>();
        params.put("item_id", itemId);
        params.put("file_size_kb", fileSizeKb);
        params.put("upload_duration_ms", uploadDurationMs);
        trackEvent("photo_uploaded", params);
    }

    // 9. API Call Success
    public void trackApiCallSuccess(String endpoint, long latencyMs, long responseSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("endpoint", endpoint);
        params.put("latency_ms", latencyMs);
        params.put("response_size", responseSize);
        trackEvent("api_call_success", params);
    }

    // 10. API Call Error
    public void trackApiCallError(String endpoint, String errorCode, int httpStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("endpoint", endpoint);
        params.put("error_code", errorCode);
        params.put("http_status", httpStatus);
        trackEvent("api_call_error", params);
    }

    // 11. SQLite Operation
    public void trackSqliteOperation(String operation, String table, int rowsAffected) {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", operation);
        params.put("table", table);
        params.put("rows_affected", rowsAffected);
        trackEvent("sqlite_operation", params);
    }

    // 12. Logout
    public void trackLogout(long sessionDurationMin, String reason) {
        Map<String, Object> params = new HashMap<>();
        params.put("session_duration_min", sessionDurationMin);
        params.put("reason", reason);
        trackEvent("logout", params);
    }

    // ================== USER PROPERTIES ==================

    public void setUserProperty(String key, String value) {
        Log.d("Analytics", "👤 User Property: " + key + " = " + value);
    }

    public void setUserRole(String role) {
        setUserProperty("user_role", role);
    }

    public void setPlanTier(String planTier) {
        setUserProperty("plan_tier", planTier);
    }

    public void setUsesCamera(boolean usesCamera) {
        setUserProperty("uses_camera", String.valueOf(usesCamera));
    }

    public void setApiEnv(String apiEnv) {
        setUserProperty("api_env", apiEnv);
    }

    public void setDeviceType(String deviceType) {
        setUserProperty("device_type", deviceType);
    }
}