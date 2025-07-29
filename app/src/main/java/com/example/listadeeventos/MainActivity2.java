package com.example.listadeeventos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.listadeeventos.R;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity2 extends AppCompatActivity {

    private TextInputEditText txtNombre, txtApellido, txtEdad;
    private Button btnGuardarPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtEdad = findViewById(R.id.txtEdad);
        btnGuardarPersona = findViewById(R.id.btnGuardarPersona);

        btnGuardarPersona.setOnClickListener(v -> {
            guardarPersona();
        });
    }

    private void guardarPersona() {
        String nombre = txtNombre.getText().toString().trim();
        String apellido = txtApellido.getText().toString().trim();
        String edad = txtEdad.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            txtNombre.setError("El nombre es requerido");
            return;
        }
        if (TextUtils.isEmpty(apellido)) {
            txtApellido.setError("El apellido es requerido");
            return;
        }
        if (TextUtils.isEmpty(edad)) {
            txtEdad.setError("La edad es requerida");
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("nombre", nombre);
        resultIntent.putExtra("apellido", apellido);
        resultIntent.putExtra("edad", edad);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}