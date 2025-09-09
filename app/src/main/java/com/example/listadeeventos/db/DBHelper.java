package com.example.listadeeventos.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.listadeeventos.network.Performance_Monitoring;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "login.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password_hash TEXT NOT NULL, salt TEXT NOT NULL, created_at INTEGER NOT NULL)");

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX idx_users_username ON users(username)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        onCreate(sqLiteDatabase);
    }

    public long insertUser(String username, char[] password) throws Exception{
        byte[] salt = PasswordUtils.generateSalt();
        byte[] hash = PasswordUtils.hash(password, salt);

        ContentValues values = new ContentValues();
        values.put("username", username.trim());
        values.put("password_hash", PasswordUtils.toBase64(hash));
        values.put("salt", PasswordUtils.toBase64(salt));
        values.put("created_at", System.currentTimeMillis());

        SQLiteDatabase db = getWritableDatabase();
        return db.insert("users", null, values);
    }

    public boolean validateUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT 1 FROM users WHERE username = ? LIMIT 1",
                new String[]{username.trim()})) {
            return cursor.moveToFirst();
        }
    }

    public boolean checkLogin(String username, char[] password_hash) throws Exception {
        SQLiteDatabase db = getReadableDatabase();
        long currentTime = System.nanoTime();
        Performance_Monitoring.getInstance().startTrace("checkLogin");

        try(Cursor cursor = db.rawQuery(
                "SELECT password_hash, salt FROM users WHERE username = ? LIMIT 1",
                new String[]{username.trim()})){

            if(!cursor.moveToFirst()) {
                return false;
            }

            String hash864 = cursor.getString(0);
            String salt864 = cursor.getString(1);

            byte[] hash = PasswordUtils.fromBase64(hash864);
            byte[] salt = PasswordUtils.fromBase64(salt864);

            boolean result = cursor.getCount() > 0;

            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - currentTime);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("duration_ms", duration);
            metadata.put("username", username);
            metadata.put("succes", result);
            Performance_Monitoring.getInstance().endTrace("checkLogin", metadata);

            return PasswordUtils.verify(password_hash, salt, hash);
        } catch (Exception e) {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - currentTime);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("duration_ms", duration);
            metadata.put("username", username);
            metadata.put("error", e.getMessage());
            Performance_Monitoring.getInstance().endTrace("checkLogin", metadata);
            throw e;
        }
    }
}