package com.example.listadeeventos.Login;


import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.listadeeventos.R;
import com.example.listadeeventos.db.DBHelper;

public class Register extends AppCompatActivity {
    private EditText etUserReg;
    private EditText etPasswordReg;
    private EditText etPassConfirm;
    private Button btnCrearCuenta;
    private Button btnBack;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dbHelper = new DBHelper(this);

        etUserReg = findViewById(R.id.etUserReg);
        etPasswordReg = findViewById(R.id.etPasswordReg);
        etPassConfirm = findViewById(R.id.etPassConfirm);

        etPasswordReg.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnBack = findViewById(R.id.btnBack);
        btnCrearCuenta.setOnClickListener(v -> doRegister());
        btnBack.setOnClickListener(v -> finish());
    }

    private void doRegister() {
        String username = etUserReg.getText().toString().trim();
        String password = etPasswordReg.getText().toString();
        String passConfirm = etPassConfirm.getText().toString();

        if (username.isEmpty() || password.isEmpty() || passConfirm.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passConfirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.length() < 3 || password.length() < 6) {
            Toast.makeText(this, "El nombre de usuario debe tener al menos 3 caracteres y la contraseña al menos 6", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (dbHelper.validateUser(username)) {
                Toast.makeText(this, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = dbHelper.insertUser(username, password.toCharArray());
            if (id>0){
                Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();
        }
    }
}
