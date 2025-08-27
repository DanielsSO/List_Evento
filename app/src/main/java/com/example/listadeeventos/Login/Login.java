package com.example.listadeeventos.Login;

import android.content.Intent;
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

public class Login extends AppCompatActivity {
    private EditText etUser;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private DBHelper dbHelper;

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
        try {
            boolean ok = dbHelper.checkLogin(username, passwordChar);
            if (ok) {
                Toast.makeText(this, "Bienvenido " + username, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Login.this, Lista_Eventos.class);
                i.putExtra("username", username);
                startActivity(i);
                finish();
            } if (!ok){
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ocurrio un error al iniciar sesion", Toast.LENGTH_SHORT).show();
        } finally {
            java.util.Arrays.fill(passwordChar, '\u0000');
        }
    }
}