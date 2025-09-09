package com.example.listadeeventos.network;

import java.util.Map;

public class logControllerLevel {
    private static int currentLogLevel = loggin_wraper.DEBUG;

    public static void setLogLevel(int level) {
        currentLogLevel = level;
    }

    public static boolean shouldLog(int level) {
        return level >= currentLogLevel;
    }

    public static void log(int level, String tag, String message, Map<String, Object> payload) {
        if (!logControllerLevel.shouldLog(level)) {
            return;
        }
    }
}
