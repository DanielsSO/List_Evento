package com.example.listadeeventos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.listadeeventos.R;
import com.google.android.material.textfield.TextInputEditText;

public class Formulario_evento extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int PICK_IMAGE_REQUEST = 102;
    private TextInputEditText txtNombre, txtFecha, txtHora;
    private Button btnGuardarEvento;
    private Button btnSeleccionarImagen;
    private ImageView imgEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.form_evento);
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
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        btnGuardarEvento = findViewById(R.id.btnGuardarEvento);

        btnGuardarEvento.setOnClickListener(v -> {
            guardarEvento();
        });

        btnSeleccionarImagen = findViewById(R.id.btnImagen);
        imgEvento = findViewById(R.id.imgEvento);
        btnSeleccionarImagen.setOnClickListener(v -> {
            verificarPermisosYSeleccionarImagen();
        });
    }

    private void verificarPermisosYSeleccionarImagen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }else {
                abrirGaleria();
            }
        }else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }else {
                abrirGaleria();
            }
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgEvento.setImageURI(data.getData());
        }
    }
    private void guardarEvento() {
        String nombre = txtNombre.getText().toString().trim();
        String fecha = txtFecha.getText().toString().trim();
        String hora = txtHora.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            txtNombre.setError("El nombre es requerido");
            return;
        }
        if (TextUtils.isEmpty(fecha)) {
            txtFecha.setError("La fecha es requerida");
            return;
        }
        if (TextUtils.isEmpty(hora)) {
            txtHora.setError("La hora es requerida");
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("nombre", nombre);
        resultIntent.putExtra("fecha", fecha);
        resultIntent.putExtra("hora", hora);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
