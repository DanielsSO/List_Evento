package com.example.listadeeventos.network;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class loggin_wraper {
    // Niveles de log
    public static final int VERBOSE = Log.VERBOSE;
    public static final int DEBUG = Log.DEBUG;
    public static final int INFO = Log.INFO;
    public static final int WARN = Log.WARN;
    public static final int ERROR = Log.ERROR;

    // Tags predefinidos para diferentes módulos
    public static final String TAG_NETWORK = "NETWORK";
    public static final String TAG_DATABASE = "DATABASE";
    public static final String TAG_SECURITY = "SECURITY";
    public static final String TAG_UI = "UI";
    public static final String TAG_BUSINESS = "BUSINESS";

    private static final String SENSITIVE_PLACEHOLDER = "***";
    private static final String[] SENSITIVE_KEYS = {
            "password", "token", "auth", "credential", "key",
            "secret", "email", "phone", "address", "dni"
    };

    // Log básico con tag y mensaje
    public static void log(int level, String tag, String message) {
        log(level, tag, message, null);
    }

    // Log con payload (evita datos sensibles)
    public static void log(int level, String tag, String message, Map<String, Object> payload) {
        if (payload != null) {
            Map<String, Object> safePayload = sanitizePayload(payload);
            String jsonPayload = convertToJson(safePayload);
            message = message + " | Payload: " + jsonPayload;
        }

        switch (level) {
            case VERBOSE:
                Log.v(tag, message);
                break;
            case DEBUG:
                Log.d(tag, message);
                break;
            case INFO:
                Log.i(tag, message);
                break;
            case WARN:
                Log.w(tag, message);
                break;
            case ERROR:
                Log.e(tag, message);
                break;
            default:
                Log.d(tag, message);
        }
    }

    // Métodos de conveniencia para diferentes niveles
    public static void info(String tag, String message) {
        log(INFO, tag, message, null);
    }

    public static void info(String tag, String message, Map<String, Object> payload) {
        log(INFO, tag, message, payload);
    }

    public static void warn(String tag, String message) {
        log(WARN, tag, message, null);
    }

    public static void warn(String tag, String message, Map<String, Object> payload) {
        log(WARN, tag, message, payload);
    }

    public static void error(String tag, String message) {
        log(ERROR, tag, message, null);
    }

    public static void error(String tag, String message, Map<String, Object> payload) {
        log(ERROR, tag, message, payload);
    }

    public static void error(String tag, String message, Throwable throwable) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("exception", throwable.getClass().getSimpleName());
        payload.put("message", throwable.getMessage());
        log(ERROR, tag, message, payload);
    }

    // Sanitiza el payload para evitar datos sensibles
    private static Map<String, Object> sanitizePayload(Map<String, Object> originalPayload) {
        Map<String, Object> safePayload = new HashMap<>();

        for (Map.Entry<String, Object> entry : originalPayload.entrySet()) {
            String key = entry.getKey().toLowerCase();
            Object value = entry.getValue();

            // Verificar si la key es sensible
            boolean isSensitive = false;
            for (String sensitiveKey : SENSITIVE_KEYS) {
                if (key.contains(sensitiveKey)) {
                    isSensitive = true;
                    break;
                }
            }

            if (isSensitive) {
                safePayload.put(entry.getKey(), SENSITIVE_PLACEHOLDER);
            } else {
                safePayload.put(entry.getKey(), value);
            }
        }

        return safePayload;
    }

    // Convierte Map a JSON string
    private static String convertToJson(Map<String, Object> payload) {
        try {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Object> entry : payload.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            return jsonObject.toString();
        } catch (Exception e) {
            return "{\"error\": \"Failed to convert to JSON\"}";
        }
    }

    // Métodos específicos para flujos críticos

    public static void logLoginFlow(String message, Map<String, Object> payload) {
        info(TAG_SECURITY, "[LOGIN_FLOW] " + message, payload);
    }

    public static void logNetworkCall(String url, String method, int statusCode, long duration) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("url", url);
        payload.put("method", method);
        payload.put("status_code", statusCode);
        payload.put("duration_ms", duration);

        info(TAG_NETWORK, "[API_CALL] " + method + " " + url, payload);
    }

    public static void logDatabaseOperation(String operation, String table, long duration) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("operation", operation);
        payload.put("table", table);
        payload.put("duration_ms", duration);

        info(TAG_DATABASE, "[DB_OP] " + operation + " on " + table, payload);
    }

    public static void logBusinessFlow(String flowName, String step, Map<String, Object> context) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("flow", flowName);
        payload.put("step", step);
        if (context != null) {
            payload.putAll(context);
        }

        info(TAG_BUSINESS, "[BUSINESS] " + flowName + " - " + step, payload);
    }
}
