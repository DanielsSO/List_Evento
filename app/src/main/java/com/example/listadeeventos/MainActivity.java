package com.example.listadeeventos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadeeventos.Adapters.PersonaAdapter;
import com.example.listadeeventos.Adapters.PersonaAdapter.OnItemActionListener;
import com.example.listadeeventos.Models.Persona;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemActionListener {

    private RecyclerView recyclerPersonas;
    private PersonaAdapter personaAdapter;
    private List<Persona> listaPersonas;
    private MaterialButton btnAgregar;
    private MaterialToolbar toolbar;

    private ActivityResultLauncher<Intent> addPersonaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addPersonaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String nombre = data.getStringExtra("nombre");
                            String apellido = data.getStringExtra("apellido");
                            String edad = data.getStringExtra("edad");

                            Persona nuevaPersona = new Persona(nombre, apellido, edad);
                            listaPersonas.add(nuevaPersona);
                            personaAdapter.notifyItemInserted(listaPersonas.size() - 1);
                            recyclerPersonas.scrollToPosition(listaPersonas.size() - 1);
                            Toast.makeText(this, "Persona " + nombre + " agregada.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        recyclerPersonas = findViewById(R.id.recyclerPersonas);
        recyclerPersonas.setLayoutManager(new LinearLayoutManager(this));

        listaPersonas = new ArrayList<>();
        listaPersonas.add(new Persona("Maria", "Gonzalez", "30"));
        listaPersonas.add(new Persona("Pedro", "Lopez", "28"));

        personaAdapter = new PersonaAdapter(listaPersonas, this);
        recyclerPersonas.setAdapter(personaAdapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.listadeeventos.MainActivity2.class);
            addPersonaLauncher.launch(intent);
        });
    }

    @Override
    public void onDeleteClick(Persona persona, int position) {
        personaAdapter.removeItem(position);
        Toast.makeText(this, "Elemento eliminado: " + persona.getNombre(), Toast.LENGTH_SHORT).show();
    }
}