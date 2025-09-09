package com.example.listadeeventos.Login;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.listadeeventos.Lista_Eventos;
import com.example.listadeeventos.MainActivity;
import com.example.listadeeventos.R;
import com.example.listadeeventos.db.DBHelper;
import com.example.listadeeventos.network.Analytics;
import com.example.listadeeventos.network.Performance_Monitoring;
import com.example.listadeeventos.network.loggin_wraper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {
    private EditText etUser;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private DBHelper dbHelper;
    private int loginAttemptCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHelper = new DBHelper(this);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> doLogin());
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(Login.this, Register.class)));
    }

    private void doLogin() {
        String username = etUser.getText().toString().trim();
        char[] passwordChar = etPassword.getText().toString().toCharArray();

        if (username.isEmpty() || passwordChar.length == 0) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        //Log flujo login
        Map<String, Object> loginStartPayload = new HashMap<>();
        loginStartPayload.put("username", username);
        loginStartPayload.put("attempt_count", loginAttemptCount);
        loggin_wraper.logLoginFlow("Iniciando proceso de login", loginStartPayload);

        //Inicia performance
        loginAttemptCount++;
        long startTime = System.nanoTime();
        Performance_Monitoring.getInstance().startLoginFlowTrace();

        try {
            boolean ok = dbHelper.checkLogin(username, passwordChar);
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            loggin_wraper.logDatabaseOperation("CheckLogin", "users", duration);

            if (ok) {
                //Log exito
                Map<String, Object> loginSuccessPayload = new HashMap<>();
                loginSuccessPayload.put("username", username);
                loginSuccessPayload.put("duration_ms", duration);
                loggin_wraper.logLoginFlow("Login exitoso", loginSuccessPayload);

                //Analytics
                Analytics.getInstance().trackLoginSuccess(username, duration, "database");
                Analytics.getInstance().setUserRole("user");

                Performance_Monitoring.getInstance().endLoginFlowTrace(true, duration, username);
                Toast.makeText(this, "Bienvenido " + username, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Login.this, Lista_Eventos.class);
                i.putExtra("username", username);
                startActivity(i);
                finish();
            } else {
                //fallo
                Map<String, Object> loginFail = new HashMap<>();
                loginFail.put("username", username);
                loginFail.put("duration_ms", duration);
                loginFail.put("attempt_count", loginAttemptCount);
                loginFail.put("error", "invalid_credentials");
                loggin_wraper.logLoginFlow("Login fallido", loginFail);

                //Analytics
                Analytics.getInstance().trackLoginFail(username, "invalid_credentials", loginAttemptCount);

                Performance_Monitoring.getInstance().endLoginFlowTrace(false, duration, username, "Credenciales incorrectas");
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            //log
            Map<String, Object> loginFail = new HashMap<>();
            loginFail.put("username", username);
            loginFail.put("duration_ms", duration);
            loginFail.put("attempt_count", loginAttemptCount);
            loggin_wraper.error(loggin_wraper.TAG_SECURITY, "Error al iniciar sesion", e);

            //Analytics
            Analytics.getInstance().trackLoginFail(username, e.getMessage(), loginAttemptCount);
            Performance_Monitoring.getInstance().endLoginFlowTrace(false, duration, username, e.getMessage());
            Toast.makeText(this, "Ocurrio un error al iniciar sesion", Toast.LENGTH_SHORT).show();
        } finally {
            java.util.Arrays.fill(passwordChar, '\u0000');
        }
    }
}