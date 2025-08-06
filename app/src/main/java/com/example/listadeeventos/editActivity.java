package com.example.listadeeventos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class editActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int PICK_IMAGE_REQUEST = 102;
    EditText etNombre, etApellido, etEdad;
    Button btnEditarPersona;
    int position = -1;

    private Button btnSeleccionarImagen;
    private ImageView imgPersona;

    private Uri imagenSeleccionadaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
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

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etEdad = findViewById(R.id.etEdad);
        btnEditarPersona = findViewById(R.id.btnEditarPersona);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String apellido = intent.getStringExtra("apellido");
        String edad = intent.getStringExtra("edad");
        String imagen = intent.getStringExtra("imagen");
        position = intent.getIntExtra("position", -1);


        etNombre.setText(nombre);
        etApellido.setText(apellido);
        etEdad.setText(edad);

        if (imagen != null) {
            imagenSeleccionadaUri = Uri.parse(imagen);
            imgPersona.setImageURI(imagenSeleccionadaUri);
        }
        btnEditarPersona.setOnClickListener(v -> {
            editarPersona();
        });

        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        imgPersona = findViewById(R.id.imgPersona);
        btnSeleccionarImagen.setOnClickListener(v -> {
            verificarPermisosYSeleccionarImagen();
        });
    }

    private void verificarPermisosYSeleccionarImagen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }else {
                abrirGaleria();
            }
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
            Uri selectedImageUri = data.getData();
            imgPersona.setImageURI(data.getData());
        }
    }

    private void editarPersona() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("El nombre es requerido");
            return;
        }
        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError("El apellido es requerido");
            return;
        }
        if (TextUtils.isEmpty(edad)) {
            etEdad.setError("La edad es requerida");
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("nombre", nombre);
        resultIntent.putExtra("apellido", apellido);
        resultIntent.putExtra("edad", edad);

        if (position != -1) {
            resultIntent.putExtra("position", position);
        }

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}